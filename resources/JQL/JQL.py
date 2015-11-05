#coding=utf-8
import re, io, os, sys, time
from JQLCore import AgentLogTableTitles, ColumnDefinition, QueryDefinition
from JQLCore import CompareItem, JQL_COMPARE, SortItem


START_TIME_PATTERN = re.compile(r"startTime:{.*}")
END_TIME_PATTERN = re.compile(r"endTime:{.*}")
CLASS_NAME_PATTERN = re.compile(r"className:{.*}")
METHOD_NAME_PATTERN = re.compile(r"methodName:.*")
COUNTER_PATTERN = re.compile(r"counter.*")
TIME_PATTERN = re.compile(r"time.*")
EARLIEST_TIME = time.strptime('1970-01-01 12:00:00', '%Y-%m-%d %H:%M:%S')
JQL_SPLIT_KEY = r"select|from|where|order by|limit|;"
ORDER_PATTERN = re.compile(r"asc|desc")

def checkJQL(jql):
    """校验jql
    
    校验规则：
        1. 关键字顺序必须为：select, from, where, order by, limit
        2. from为必填项
    """
    
    if len(jql.strip()) == 0:
        raise Exception("JQL empty error")
    
    max = -1
    pos = jql.find("select")
    if pos != -1:
        if pos > max:
            max = pos
        else:
            raise Exception('select order error')
            
    pos = jql.find("from")
    if pos != -1:
        if pos > max:
            max = pos
        else:
            raise Exception('from order error')
    else:
        raise Exception('no from error')
        
    pos = jql.find("where")
    if pos != -1:
        if pos > max:
            max = pos
        else:
            raise Exception('where order error')
             
    pos = jql.find("order by")
    if pos != -1:
        if pos > max:
            max = pos
        else:
            raise Exception('order by order error')
    
    pos = jql.find("limit")
    if pos != -1:
        if pos > max:
            max = pos
        else:
            raise Exception('limit order error')

def parseJQL(jql):
    """解析查询语句
    
    Args:
        jql: 查询语句
    Return:
        QueryDefinition: 解析查询语句得到的定义
    """
    
    #分片
    segments = re.split(JQL_SPLIT_KEY, jql)
    
    i = 1
    #处理select
    titleList = []
    if jql.find("select") != -1:
        selectStr = segments[i].strip()
        i += 1
        
        if len(selectStr) == 0:
            raise Exception('select no values error')
            
        for title in selectStr.split(","):
            title = title.strip()
            
            if len(title) == 0:
                raise Exception('selct empty value error: select ' + selectStr) 
            
            if title not in AgentLogTableTitles:
                raise Exception('select value error:\'' + title + '\'')
                
            titleList.append(title)
    else:
        # 默认表头
        titleList = AgentLogTableTitles

    #处理from
    fileList = []
    if jql.find("from") != -1:
        fromStr = segments[i].strip()
        i += 1
        
        if len(fromStr) == 0:
            raise Exception('from no values error')
        
        for root in fromStr.split(","):
            root = root.strip()
            if len(root) == 0:
                raise Exception('from empty value error: where ' + fromStr)
                
            if os.path.isdir(root):
                files = os.listdir(root)
                for file in files:
                    fileList.append(os.path.join(root, file))
            elif os.path.isfile(root):
                fileList.append(root)
            else:
                raise Exception('from value error: ' + root + ' is not a file or dir')
    else:
        raise Exception('no from error')
        
    #处理where
    encoding = "utf-8" 
    compareList = []
    if jql.find("where") != -1:
        whereStr = segments[i].strip()
        i += 1
        
        if len(whereStr) == 0:
            raise Exception('where no values error')
            
        #得到文件编码、过滤条件
        filterList = []
        for param in whereStr.split("and"):
            elements = param.split('=')
            key = elements[0].strip()
            if key == "encoding":
                encoding = elements[1].strip()
            else:
                filterList.append(param)

        #根据过滤得到比较列表
        for filter in filterList:
            operator = None
            if filter.find(">=") != -1:
                operator = ">="
            elif filter.find("<=") != -1:
                operator = "<="
            elif filter.find("=") != -1:
                operator = "="
            elif filter.find(">") != -1:
                operator = ">"
            elif filter.find("<") != -1:
                operator = "<"
            else:
                continue
        
            elements = filter.split(operator)
            if len(elements) != 2:
                continue
                
            ci = CompareItem(elements[0].strip(), operator, elements[1].strip())
            compareList.append(ci)
        
    #处理order by
    keyList = []
    orderList = []
    if jql.find("order by") != -1:
        orderByStr = segments[i].strip()
        i += 1
        
        if len(fromStr) == 0:
            raise Exception('order by no values error')
         
        for order in orderByStr.split(","):
            order = order.strip()
            
            if len(order) == 0:
                raise Exception('order by empty values error')     
            else:
                pos = re.search(ORDER_PATTERN, order)
                key = None
                keyOrder = None
                if pos is None:
                    # 默认升序
                    key = order
                    keyOrder = "asc"
                else:
                    key = order[: pos.start()].strip()
                    keyOrder = order[pos.start() : pos.end()]
         
                if key not in AgentLogTableTitles:
                    raise Exception('order by no support key error. key = ' + key)
                    
                keyList.append(key)
                orderList.append(keyOrder)
    
    #处理limit
    limit = -1
    if jql.find("limit") != -1:
        limitStr = segments[i].strip()
        i += 1
         
        if len(limitStr) == 0:
            raise Exception('limit no values error')
        
        if limitStr.isdigit() == False:
            raise Exception("limit value type error, value must be a nonnegative integer , value = " + limitStr)
        
        limit = limitStr
    
    return QueryDefinition(titleList, fileList, encoding, keyList, orderList, compareList, limit)

def getValueInBrackets(str):
    """得到括号内的值
    
    Args:
        str: 包括{}的字符串

    Return:
        {}内的值(去除了2端的空格)
    """

    return getValueBetweenKey1AndKey2(str, "{", "}")

def getValueBetweenKey1AndKey2(str, key1, key2):
    """得到关键字1和关键字2之间的值
    
    Args:
        str: 包括key1、key2的字符串
        key1: 关键字1
        key2: 关键字2
        
    Return：
        key1 ... key2 内的值（去除了2端的空格）
    """
    
    offset = len(key1)
    start = str.find(key1) + offset
    end = str.find(key2)
    value = str[start : end]
    return value.strip()
    
def queryDatas(queryDef):
    """查询并过滤数据
    
    Args:
        queryDef: 查询定义
    Return:
        SortItem List：待排序的数据列表
    """
    
    result = []
    #查询所有文件的数据
    for file in queryDef.fileList:
        temp = []
        startTime = None
        clazz = None
        method = None
        count = 0
        totalTime = 0
        avgTime = 0
        with io.open(file, 'r', encoding=queryDef.encoding) as fd:
            for line in fd:
                #处理startTime:...行
                match = START_TIME_PATTERN.match(line)
                if match: 
                    #如果开始时间有变化，则保存结果，否则丢弃
                    newStartTime = getValueInBrackets(line)
                    if newStartTime != startTime: 
                        result.extend(temp)
                        startTime = newStartTime
                    temp = []
                    continue
                     
                #处理endTime:...行
                match = END_TIME_PATTERN.match(line)
                if match:
                    endTime = getValueInBrackets(line)
                    for item in temp:
                        item.endTime = endTime
                    continue
            
                #解析className...行
                match = CLASS_NAME_PATTERN.match(line)
                if match:
                    clazz = getValueInBrackets(line)
                    continue
                    
                #解析methodName...行
                match = METHOD_NAME_PATTERN.match(line)
                if match:
                    for str in line.split(','):
                        #处理methodName
                        match = METHOD_NAME_PATTERN.match(str)
                        if match:
                            method = getValueInBrackets(str)
                            continue
                            
                        #处理counter
                        match = COUNTER_PATTERN.match(str)
                        if match:
                            count = getValueInBrackets(str)
                            continue
                            
                         #处理time
                        match = TIME_PATTERN.match(str)
                        if match:
                            totalTime = getValueInBrackets(str)
                            continue
                    
                    item = SortItem(clazz, method, count, totalTime, startTime)
                    temp.append(item)
        
        #保存最后一个时间域的数据
        result.extend(temp)
    
    return result
   
def filterDatas(itemList, queryDef):
    """过滤数据列表
    
    Args:
        itemList: 数据
        queryDef: 查询定义
    Return:
        dataList: 过滤后的数据
    """
    
    dataList = []
    for item in itemList:
        if checkItem(item, queryDef):
            dataList.append(item)
             
    return dataList     
      
def checkItem(item, queryDef):
    """校验数据
    
    Args:
        item: 数据
        queryDef: 查询定义
    """
    
    result = True
    for ci in queryDef.compareList:    
        if ci.key == "time":
            result = JQL_COMPARE.get(ci.operator)(item.time, int(ci.value))
        elif ci.key == "count":
            result = JQL_COMPARE.get(ci.operator)(item.count, int(ci.value))
        elif ci.key == "avg time":
            result = JQL_COMPARE.get(ci.operator)(item.avgTime, int(ci.value))
        elif ci.key == "method":
            result = JQL_COMPARE.get(ci.operator)(item.method.lower(), ci.value)
        elif ci.key == "class":
            result = JQL_COMPARE.get(ci.operator)(item.clazz.lower(), ci.value)
        elif ci.key == "start time":
            result = JQL_COMPARE.get(ci.operator)(
                            time.strptime(item.startTime, '%Y-%m-%d %H:%M:%S'),
                            time.strptime(ci.value, '%Y-%m-%d %H:%M:%S')
                            )
        elif ci.key == "end time":
            result = JQL_COMPARE.get(ci.operator)(
                            time.strptime(item.endTime, '%Y-%m-%d %H:%M:%S'), 
                            time.strptime(ci.value, '%Y-%m-%d %H:%M:%S')
                            )
                            
        if result == False:
            break
        
    return result
   
def sortAndCut(itemList, queryDef):
    """将itemList根据查询条件排序
    
    Args:
        itemList: 数据
        queryDef：查询定义
    Return:
        dataList: 排序和过滤后的结果
    """
    
    #处理排序
    for key, order in zip(reversed(queryDef.keyList), reversed(queryDef.orderList)):
        #得到lambda函数
        keyLambda = None
        if key == 'count':
            keyLambda = lambda item: item.count
        elif key == 'time':
            keyLambda = lambda item: item.time
        elif key == 'avg time':
            keyLambda = lambda item: item.avgTime
        elif key == 'start time': 
            keyLambda = lambda item: time.strptime(item.startTime, '%Y-%m-%d %H:%M:%S') if item.startTime != None else EARLIEST_TIME
        elif key == 'end time':
            keyLambda = lambda item: time.strptime(item.endTime, '%Y-%m-%d %H:%M:%S') if item.endTime != None else EARLIEST_TIME
        else:
            continue
        
        #判断是否逆排序（默认递增排序）
        rev = (order == 'desc')
        
        itemList.sort(key=keyLambda, reverse=rev)

    return itemList[0 : queryDef.limit] if queryDef.limit != -1 else itemList

def drawTable(itemList, queryDef):
    """画表格
    
    Args:
        itemList: 数据
        queryDef: 查询定义
    
    Example:
    +---------+----------+-----------+------------+--------------+
    | count   | time     | avg time  | method     | class        |
    +---------+----------+-----------+------------+--------------+
    | 10      | 500      | 50        | sayHello   | Test.java    |
    +---------+----------+-----------+------------+--------------+
    """
    
    # 画表头
    drawLine(queryDef)
    drawTitle(queryDef)
    drawLine(queryDef)
   
    # 画数据行
    drawDataRows(itemList, queryDef)
    
    # 画结尾行
    drawLine(queryDef)

def drawLine(queryDef):
    """画分割线
    
    Args:
        queryDef: 查询定义
    """
    
    formatStr = ""
    valueList = []
    for title in queryDef.titleList:
        colDef = queryDef.tableCols[title]
        size = colDef.size + 2
        formatStr += ('+' + formatString("string", size, False))
        valueList.append("-"*size)
    
    formatStr += "+"
    
    print(formatStr % tuple(valueList))

def drawTitle(queryDef):
    """画表头
    
    Args:
        queryDef: 查询定义
    """
    
    formatStr = ""
    valueList = []
    for title in queryDef.titleList:
        colDef = queryDef.tableCols[title]
        formatStr += ('| ' + formatString("string", colDef.size) + ' ')
        valueList.append(title)
    
    formatStr += "|" 
    print(formatStr % tuple(valueList))
  
def drawDataRows(dataList, queryDef):
    """画数据行
    
    Args:
        itemList: 数据
        queryDef: 查询定义
    """

    for data in dataList:
        formatStr = ""
        valueList = []
        for title in queryDef.titleList:
            colDef = queryDef.tableCols[title]
            formatStr += ('| ' + formatString(colDef.type, colDef.size) + ' ')
            
            if title == 'time':
                valueList.append(data.time)
            elif title == 'count':
                valueList.append(data.count)
            elif title == 'avg time':
                valueList.append(data.avgTime)
            elif title == 'method':
                valueList.append(data.method)
            elif title == 'class':
                valueList.append(data.clazz)
            elif title == 'start time':
                valueList.append(data.startTime)
            elif title == 'end time':
                valueList.append(data.endTime)
                
        formatStr += "|"
        print(formatStr % tuple(valueList))
               
def formatString(type, size, leftAlignment=True):
    """得到格式化占位符
    
    Args:
        type: 数据类型
        size: 位数
        leftAlignment：是否左对齐
        
    Return:
        格式化占位符
    """
    
    prefix = ('-' if leftAlignment else '')
    result = '%' + prefix + str(size)
    
    if type == "int":
        result += 'd'
    elif type == "float":
        result += '.2f'
    else:
        result += 's'
        
    return result

def executeQuery(jql):
    """执行jql，并输出表格
    
    Args:
        jql: 查询语句
    """
    
    try: 
        print("")
        
        #校验jql
        none, checkTime = JQL_TIMER(checkJQL, jql)
        #解析jql
        queryDef, parseTime = JQL_TIMER(parseJQL, jql)
        #查询数据
        dataList, queryTime = JQL_TIMER(queryDatas, queryDef)
        #过滤数据
        dataList, filterTime = JQL_TIMER(filterDatas, dataList, queryDef)
        #排序、裁剪数据
        dataList, sortTime = JQL_TIMER(sortAndCut, dataList, queryDef)
        #画表
        none, drawTime = JQL_TIMER(drawTable, dataList, queryDef)
       
        total = checkTime + parseTime + queryTime + filterTime + sortTime + drawTime
        
        print("spend %.2fs(check: %.2fs, parse: %.2fs, query: %.2fs, filter: %.2fs, sort: %.2fs, draw: %.2fs)\n" % 
            (total, checkTime, parseTime, queryTime, filterTime, sortTime, drawTime))
    except Exception as err:
        msg = "[JQL] {0}".format(err)
        length = len(msg)
        print("%s\n%s\n%s\n" % ("*"*length, msg, "*"*length))
        
def JQL_TIMER(func, *args):
    """计时函数
    
    Args:
        func: 函数名
        args: 可变参数
    Return:
        函数执行结果, 函数执行时间
    """
    
    start = time.time()
    result = func(*args)
    end = time.time()
    return result, end - start
    
def main():
    """主函数"""
    
    if len(sys.argv) > 1:
        executeQuery(sys.argv[1])
    else:
        print("Please input JQL\ninput 'q!' for quit, input ';' for execute jql\n")
        jql = ""
        while (True):
            line = input(">>> ").lower()
            if (line == "q!"):
                print("Thanks.")
                sys.exit()
            elif line.find(";") != -1:
                line = line.split(";")
                jql += (' ' + line[0].strip() + ';')
                executeQuery(jql)
                jql = ""
            else:
                jql += (' ' + line.strip())
      
if __name__ == "__main__":
    main()
 
