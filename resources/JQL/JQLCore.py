#coding=utf-8

class ColumnDefinition:
    """列定义"""
    
    def __init__(self, name, type, size):
        self.name = name
        self.type = type
        self.size = int(size)

#java-agent-log-table表头定义
AgentLogTableTitles = ['time', 'count', 'avg time', 'method', 'class', 'start time', 'end time']
        
#java-agent-log-table定义
AgentLogTable = {
    'time': ColumnDefinition('time', 'int', 10),
    'count': ColumnDefinition('count', 'int', 10),
    'avg time': ColumnDefinition('avg time', 'int', 10),
    'method': ColumnDefinition('method', 'string', 35),
    'class': ColumnDefinition('method', 'string', 100),
    'start time': ColumnDefinition('start time', 'string', 20),
    'end time': ColumnDefinition('end time', 'string', 20)
}
           
class QueryDefinition:
    """查询定义
    
    将解析后的查询条件保存于此，用于查询方法间的参数传递
    """
    
    def __init__(self, titleList, fileList, encoding, keyList, orderList=[], compareList=[],  limit=-1):
        self.titleList = titleList
        self.fileList = fileList
        self.encoding = encoding
        self.keyList = keyList
        self.orderList = orderList
        self.compareList = compareList
        self.limit = int(limit)
        self.tableCols= AgentLogTable
        
class CompareItem:
    """比较项"""
    
    def __init__(self, key, operator, value):
        self.key = key
        self.operator = operator
        self.value = value

def equal(x, y):
    return x == y
    
def lessThan(x, y):
    return x < y
    
def lessThanOrEqualTo(x, y):
    return x <= y
        
def greaterThan(x, y):
    return x > y
    
def greaterThanOrEqualTo(x, y):
    return x >= y 

#jql比较操作
JQL_COMPARE = {
    "=": equal,
    "<": lessThan,
    "<=": lessThanOrEqualTo,
    ">": greaterThan,
    ">=": greaterThanOrEqualTo
}

class SortItem:
    """排序项"""
    
    def __init__(self, clazz, method, count, time, startTime, endTime=None):
        """
        Args:
            clazz: 类名
            method: 方法
            count: 方法调用总次数
            time: 方法调用总时间
            startTime: 开始时间
        """
        
        self.clazz = clazz
        self.method = method
        self.count = int(count)
        self.time = int(time)
        self.startTime = startTime
        self.endTime = endTime
        self.avgTime = float(self.time / self.count) #方法调用平均时间
 
    def copy(self):
        """复制"""

        return SortItem(self.clazz, self.method, self.count, self.time, self.startTime)
    
    def equals(self, other):
        """比较是否相等"""
        
        return (self.clazz == other.clazz 
            and self.method == other.method
            and self.count == other.count 
            and self.time == other.time
            and self.startTime == other.startTime 
            and self.avgTime == other.avgTime)
