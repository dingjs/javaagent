//验证是否为空或者多空格
	function isNull(str){ 
		if ( str == "" ) return true; 
		var regu = "^[ ]+$"; 
		var re = new RegExp(regu); 
		return re.test(str); 
	} 
	function isChinaOrLett(s){// 判断是否是汉字、字母
			var regu = "^[a-zA-Z\u4e00-\u9fa5]+$";   
			var re = new RegExp(regu); 
		if (re.test(s)) { 
			return true; 
		}else{ 
			return false; 
		} 
	}
	function isChina(s){// 判断是否是汉字
		var regu = "[\u4e00-\u9fa5]+";  
		var re = new RegExp(regu); 
		if (re.test(s)) { 
			return true; 
		}else{ 
			return false; 
		} 
	}
	function isCardNo(card)  // 是否合法的身份证号
	{  
	   // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
	   var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;  
	   if(reg.test(card))  
	   {  
	       return true;  
	   }else{
			return false
		}
	}
	//合法的IP地址
	function isIP(strIP) { 
		if (isNull(strIP)) return false; 
		var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/g // 匹配IP地址的正则表达式
		if(re.test(strIP)) 
		{ 
		if( RegExp.$1 <256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256) return true; 
		} 
		return false; 
	} 
	/*
	 * 用途：检查输入字符串是否符合正整数格式 输入： s：字符串 返回： 如果通过验证返回true,否则返回false
	 * 
	 */ 
	function isNumber( s ){   
		var regu = "^[0-9]+$"; 
		var re = new RegExp(regu); 
		if (s.search(re) != -1) { 
			return true; 
		} else { 
			return false; 
		} 
	}
	  
	/* 
	用途：检查输入的起止日期是否正确，启用时间不得早于当前时间， 
	且结束如期>=起始日期 
	输入： 
	startDate：起始日期
	endDate：结束如期
	返回： 
	如果通过验证返回true,否则返回false 

	*/ 
	function checkTwoDate( startDate,endDate ) { 
	  var now = new Date();//获得系统当前时间
	  //把时间格式转换为字符串
      var year = now.getFullYear();
      var month =(now.getMonth() + 1).toString();
      var day = (now.getDate()).toString();
      if (month.length == 1) {
    	  month = "0" + month;
      }
      if (day.length == 1) {
    	  day = "0" + day;
      }
      var dateTime = year+month+day;
      //验证
      startDate=startDate.replace("-","").replace("-","");
      endDate=startDate.replace("-","").replace("-","");
		if(startDate<dateTime){
			return false; 
		} else if( startDate>endDate) {
			return false; 
		} 
		return true; 
	} 
	/* 
	结束如期>=起始日期 
	输入： 
	startDate：起始日期
	endDate：结束如期
	返回： 
	如果通过验证返回true,否则返回false 

	 */ 
	function checkDate( startDate,endDate ) { 
		startDate=startDate.replace("-","").replace("-","");
		endDate=endDate.replace("-","").replace("-","");
		if( startDate>endDate) {
			return false; 
		} 
		return true; 
	} 
	/* 
	用途：检查输入字符串是否只由汉字、字母、数字组成 
	输入： 
	value：字符串 
	返回： 
	如果通过验证返回true,否则返回false 

	*/ 
	function isChinaOrNumbOrLett( s ){//判断是否是汉字、字母、数字组成 
		var regu = "^[0-9a-zA-Z\u4e00-\u9fa5]+$";   
		var re = new RegExp(regu); 
		if (re.test(s)) { 
			return true; 
		}else{ 
			return false; 
		} 
	} 
	/* 
	用途：检查输入字符串是否只由、字母、数字组成 
	输入： 
	value：字符串 
	返回： 
	如果通过验证返回true,否则返回false 

	 */ 
	function isNumbOrLett( s ){//判断是否是字母、数字组成 
		var regu = "^[0-9a-zA-Z]+$";   
		var re = new RegExp(regu); 
		if (re.test(s)) { 
			return true; 
		}else{ 
			return false; 
		} 
	} 
	/*
	 用途：检查输入字符串是否是http://开头
	 */
	function isServerUrl(s){
		var patrn=/^(https?|http:\/\/)/i; 
		var patrn2=/^[-\.\w#%&:?=\/]+$/i; 
		if (!patrn.exec(s)) return false; 
		if (!patrn2.exec(s)) return false ; 
		return true;
	}


/**
 * 浏览器复制功能
 */
function copy(){

    $('#copy_bar').zclip({
        path: 'ZeroClipboard.swf',
        copy: function(){//复制内容
            return $('#code_area').val();
        },
        afterCopy: function(){//复制成功
        	$.messager.show({
    			title:'复制结果',
    			msg:'已复制到剪切板。',
    			timeout:3000,
    			showType:'slide',
    			style:{
    					left:'',
    					top:'',
    					bottom:document.body.scrollTop+document.documentElement.scrollTop
    			}

    		});
        }
    });

}
/**
 * 数据库信息读取cookie，实现记住功能
 */
function readCookie(){
	$("#dbType").val(getCookie("dbType"));
	$("#dbServerIp").val(getCookie("dbServerIp"));
	$("#dbServerPort").val(getCookie("dbServerPort"));
	$("#dbUserName").val(getCookie("dbUserName"));
	$("#dbPassword").val(getCookie("dbPassword"));
	$("#dbName").val(getCookie("dbName"));
}

/**
 * 把数据库信息存进cookie，实现记住密码功能
 */
function setCookie(name,value,expireHours) {
//	var cookieString=name+"="+escape(value);
	var cookieString=name+"="+value;
    //判断是否设置过期时间
    if(expireHours>0){
           var date=new Date();
           date.setTime(date.getTime+expireHours*3600*1000);
           cookieString=cookieString+";expires="+date.toGMTString();
    }
    document.cookie=cookieString;
}
function setCookies(dbTypeValue,dbServerIpValue,dbServerPortValue,dbUserNameValue,dbPasswordValue,dbNameValue){
	setCookie("dbType",dbTypeValue,12);
	setCookie("dbServerIp",dbServerIpValue,12);
	setCookie("dbServerPort",dbServerPortValue,12);
	setCookie("dbUserName",dbUserNameValue,12);
	setCookie("dbPassword",dbPasswordValue,12);
	setCookie("dbName",dbNameValue,12);
}

function getCookie(name){
    var strCookie=document.cookie;
    var arrCookie=strCookie.split("; ");
    for(var i=0;i<arrCookie.length;i++){
          var arr=arrCookie[i].split("=");
          if(arr[0]==name)return arr[1];
    }
    return "";
}

function deleteCookies(dbType,dbServerIp,dbServerPort,dbUserName,dbPassword,dbName){
	deleteCookie(dbType);
	deleteCookie(dbServerIp);
	deleteCookie(dbServerPort);
	deleteCookie(dbUserName);
	deleteCookie(dbPassword);
	deleteCookie(dbName);
}
function deleteCookie(name){
    var date=new Date();
    date.setTime(date.getTime()-10000);
    document.cookie=name+"=v; expire="+date.toGMTString();
}

//点击+号，加表
function addTable(){
	$("#addTabel").click(function(){ 
		 if(!$("#conn").linkbutton('options').disabled)//获取该按钮的启用、禁用状态
			{
				 $.messager.alert('提示信息','请先连接数据库！','error');
				 return false;
			} 
    	$.messager.progress({
			title:"提示框",
			text:"正在尝试连接，请稍候..."
			}); 
	    	    
   	var TableNameArray = [];
   	var temp = [];
	
    	//获取树形下的所有父节点，得到父节点对象，把里面的text获取到，然后封装新的对象，然后手动关联表。
		var anArray_f = $("#table_tree").tree("getRoots");
			//.遍历anArray_f
			for ( var i = 0; i < anArray_f.length; i++) {
				var tableName=anArray_f[i].text;
				temp.push(tableName);
			} 
			TableNameArray.push(temp.join(";"));

		 	$('#tableInfoList').datagrid({
				 	title:'', 
				 	 method:$("#db_form").attr("method"),
				     url:_path+"/getAllTableName.do", 
				     //queryParams:$("#db_form").serialize(),//此方法不适应datagrid方法
				     queryParams:{
							dbType:$("#dbType").combobox('getText'),
							dbServerIp:$("#dbServerIp").attr("value"),
							dbServerPort:$("#dbServerPort").attr("value"),
							dbUserName:$("#dbUserName").attr("value"),
							dbPassword:$("#dbPassword").attr("value"),
							dbName:$("#dbName").attr("value"),
							selectTable:TableNameArray
				        },
				     
				    // fit:true,
		            //pageSize : 5,//默认选择的分页是每页5行数据
		           	//pageList : [ 5, 10, 15, 20 ],//可以选择的分页集合
		           	//pageNumber:1,
		          //  nowrap : true,//设置为true，当数据长度超出列宽时将会自动截取
		            striped : true,//设置为true将交替显示行背景。
		            //collapsible : true,//显示可折叠按钮
		            //toolbar:"#tbRes",//在添加 增添、删除、修改操作的按钮要用到这个
		            loadMsg : '数据装载中......',
		            //singleSelect:false,//为true时只能选择单行
		            rownumbers: true,
		            singleSelect: false,
					selectOnCheck: true,
					checkOnSelect: true,
		          //  fitColumns :false,//允许表格自动缩放，以适应父容器
		           // sortName : 'resName',//当数据表格初始化时以哪一列来排序
		            //sortOrder : 'asc',//定义排序顺序，可以是'asc'或者'desc'（正序或者倒序）。
		            //remoteSort : false, //定义从服务器对数据进行排序。
			         columns:[[
								{field:'checkbox',checkbox:true },//显示复选框样式，单独列出来一列，json返回内容不需包括
								{field:'tableName',title:'Table' }
								]],
			        	 //onLoadSuccess:function()，判断返回值，但返回值必须是json格式。并且是{'rows':[{'tableName':'连接不成功'}]}这种格式
			        onLoadSuccess:function(data){ //JSON.stringify(data) 方法把data转为字符串，str.indexOf(str2)!=-1;判断是否包含某字符串
			        	$.messager.progress('close');
			        	 if(JSON.stringify(data).indexOf("连接不成功") != -1){
			        		$.messager.alert('连接结果',"连接不成功，请检查参数和服务器状态",'error');
			        	}else {
			        		$('#win').window('open');

			        	} 
			        }
			       /*    data:[
			       		{tableName:tableName,},
			    	]  */

			       /*  pagination:true,//分页控件  
			        //rownumbers:true,//行号 
			         // frozenColumns:[[ 
						             {field:'idAmsResInfo',checkbox:true} 
						        ]],  
			        toolbar:"#tbRes"  */
			    });	
	});
}
//点击-号，减表
function delTable(){
	$("#delTabel").click(function(){ 
		$.messager.confirm('确认对话框', '您确定要删除当前表吗？', function(r){
			if (r){
				$("#proportion_td").show();
				$("#proportionText").css("color","black");
			}else{

			return false;

			}
		});
	});
}
/**
 * 常用端口根据数据库类型动态变化
 */
function portChangeByDbType(){
	$("#dbType").combobox({
		onSelect:function(){ 
			var dbType = $("#dbType").combobox('getText');
			if (dbType == "SqlServer") {
				$("#dbServerPort").val("1433");
			}else if(dbType == "Mysql"){
				$("#dbServerPort").val("3306");
			}else if(dbType == "Abase"){
				$("#dbServerPort").val("5432");
			}else{
				$("#dbServerPort").val("");
			}
			
	 	}
	}); 
}
/*
 * 导出配置
 */
function exportInfo(){
	//导出配置，模拟下载文件的写法。
  	$("#export").click(function(){ 
  		 if(!$("#conn").linkbutton('options').disabled)//获取该按钮的启用、禁用状态
			{
				 $.messager.alert('提示信息','请先连接数据库！','error');
				 return false;
			}
  		 
		 //在导出之前，先要保存当前业数据；读的当前页面的值。
 		var tableName_1=$("#tableName").val();//当前页面表名，而不是当前点击的
 		var columnName_1=$("#columnName").val();//当前页面列名，而不是当前点击的
 		var dataType_1=$("#dataType").val();//数据类型
 		var maxLength_1=$("#maxLength").val();//最长长度
 		var remarks_1=$("#remarks").val();//释义
 		var userDataType_1=$("#userDataType").combobox('getText')//预置类型
 		var startNo_1=$("#startNo").val();//数值开始
 		var endNo_1=$("#endNo").val();//数值结束
 		var startLength_1=$("#startLength").val();//长度开始
 		var endLength_1=$("#endLength").val();//长度结束
 		var startDate_1=$('#startDate').datebox('getValue');//开始日期
 		var endDate_1=$('#endDate').datebox('getValue');//结束日期
 		var diy_1=$("#diy").val();//自定义字符
 		var sql_1=$("#sql").val();//SQL语句
 		var isUnique_1=$("#isUnique").is(":checked"); //是否唯一值，返回false/true
 		var isManualCorrelation_1=$("#isManualCorrelation").is(":checked");//判断是否手动关联
 		var manualCorrelation_table_1 =$("#manualCorrelation_table").combobox('getText');//手动表
 		var manualCorrelation_column_1 =$("#manualCorrelation_column").combobox('getText');//手动列
 		var pkTable_name_1=$("#pkTable_name").val();//关联表
 		var pkColumn_name_1=$("#pkColumn_name").val();//关联列
 		//新加插入比例
 		var isProportion_1=$("#isProportion").is(":checked");//判断是否勾选了插入比例
 		var isMax_1=$("#isMax").is(":checked");//判断是否勾选了插入随机副表次数
 		var insertAppendTimes_1=$("#insertAppendTimes").val();//获取插入的值
 		
 		var isContain_1=$("#isContain_").is(":checked");//判断是否勾选了包含
 		var isConnect_1=$("#isConnect").is(":checked");//判断是否勾选了拼接
 		var keyWords_text_1=$("#keyWords_text").val();//获取标识符
 		//判断有没有这个对象，如果有，就先删除，再存，如果没有，就直接存进去。
 		var ifExistObj=findObjectInArray(jsonArray, columnName_1, tableName_1);
 		if (ifExistObj) {
 			jsonArray.splice($.inArray(ifExistObj,jsonArray), 1);//移除指定索引；$.inArray(ifExistObj,jsonArray)返回当前obj的索引
 		}
 		//不管删不删除以前的，都要添加当前页面数据。
 		if (userDataType_1 == "数字") {
 				var jsonObj ={
 					'tableName': tableName_1,
 					'columnName':columnName_1,
 					"dataType":dataType_1,
 					"maxLength":maxLength_1,
 					"remarks":remarks_1,
 					"userDataType":userDataType_1,
 					"isUnique":isUnique_1,
 					"pkTable_name":pkTable_name_1,
 					"pkColumn_name":pkColumn_name_1,
 					"isManualCorrelation":isManualCorrelation_1,
 					"manualCorrelation_table":manualCorrelation_table_1,
 					"manualCorrelation_column":manualCorrelation_column_1,
 					"isProportion":isProportion_1,//新加的插入比例
 					"isMax":isMax_1,//新加的随机副表次数
 					"insertAppendTimes":insertAppendTimes_1,
 					"isContain_":isContain_1,//新加的包含
 					"isConnect":isConnect_1,//新加的拼接
 					"keyWords_text":keyWords_text_1,//拼接标识符
 					
 					"startNo":startNo_1,
 					"endNo":endNo_1
 				};
 			
 			}else if(userDataType_1=="自定义字符" ) {
 				var jsonObj ={
 					'tableName': tableName_1,
 					'columnName':columnName_1,
 					"dataType":dataType_1,
 					"maxLength":maxLength_1,
 					"remarks":remarks_1,
 					"userDataType":userDataType_1,
 					"isUnique":isUnique_1,
 					"pkTable_name":pkTable_name_1,
 					"pkColumn_name":pkColumn_name_1,
 					"isManualCorrelation":isManualCorrelation_1,
 					"manualCorrelation_table":manualCorrelation_table_1,
 					"manualCorrelation_column":manualCorrelation_column_1,
 					"isProportion":isProportion_1,//新加的插入比例
 					"isMax":isMax_1,//新加的随机副表次数
 					"insertAppendTimes":insertAppendTimes_1,
 					"isContain_":isContain_1,//新加的包含
 					"isConnect":isConnect_1,//新加的拼接
 					"keyWords_text":keyWords_text_1,//拼接标识符
 					"diy":diy_1
 				};
 		
 			}else if(userDataType_1=="SQL语句"){
 				var jsonObj ={
 					'tableName': tableName_1,
 					'columnName':columnName_1,
 					"dataType":dataType_1,
 					"maxLength":maxLength_1,
 					"remarks":remarks_1,
 					"userDataType":userDataType_1,
 					"isUnique":isUnique_1,
 					"pkTable_name":pkTable_name_1,
 					"pkColumn_name":pkColumn_name_1,
 					"isManualCorrelation":isManualCorrelation_1,
 					"manualCorrelation_table":manualCorrelation_table_1,
 					"manualCorrelation_column":manualCorrelation_column_1,
 					"isProportion":isProportion_1,//新加的插入比例
 					"isMax":isMax_1,//新加的随机副表次数
 					"insertAppendTimes":insertAppendTimes_1,
 					"isContain_":isContain_1,//新加的包含
 					"isConnect":isConnect_1,//新加的拼接
 					"keyWords_text":keyWords_text_1,//拼接标识符
 					"sql":sql_1
 				};
 				
 			}else if(userDataType_1=="日期"){
 				var jsonObj ={
 					'tableName': tableName_1,
 					'columnName':columnName_1,
 					"dataType":dataType_1,
 					"maxLength":maxLength_1,
 					"remarks":remarks_1,
 					"userDataType":userDataType_1,
 					"isUnique":isUnique_1,
 					"pkTable_name":pkTable_name_1,
 					"pkColumn_name":pkColumn_name_1,
 					"isManualCorrelation":isManualCorrelation_1,
 					"manualCorrelation_table":manualCorrelation_table_1,
 					"manualCorrelation_column":manualCorrelation_column_1,
 					"isProportion":isProportion_1,//新加的插入比例
 					"isMax":isMax_1,//新加的随机副表次数
 					"insertAppendTimes":insertAppendTimes_1,
 					"isContain_":isContain_1,//新加的包含
 					"isConnect":isConnect_1,//新加的拼接
 					"keyWords_text":keyWords_text_1,//拼接标识符
 					
 					"startDate":startDate_1,
 					"endDate":endDate_1

 				};
 			
 			}else {
 				var jsonObj ={
 					'tableName': tableName_1,
 					'columnName':columnName_1,
 					"dataType":dataType_1,
 					"maxLength":maxLength_1,
 					"remarks":remarks_1,
 					"userDataType":userDataType_1,
 					"isUnique":isUnique_1,
 					"pkTable_name":pkTable_name_1,
 					"pkColumn_name":pkColumn_name_1,
 					"isManualCorrelation":isManualCorrelation_1,
 					"manualCorrelation_table":manualCorrelation_table_1,
 					"manualCorrelation_column":manualCorrelation_column_1,
 					"isProportion":isProportion_1,//新加的插入比例
 					"isMax":isMax_1,//新加的随机副表次数
 					"insertAppendTimes":insertAppendTimes_1,
 					"isContain_":isContain_1,//新加的包含
 					"isConnect":isConnect_1,//新加的拼接
 					"keyWords_text":keyWords_text_1,//拼接标识符
 					
 					"startLength":startLength_1,
 					"endLength":endLength_1

 				};
 			
 		}
 		
 		jsonArray.push(jsonObj); //添加单个json对象
  		 
   	    //encodeURIComponent() 作用：可把字符串作为URI 组件进行编码。
   		$("#json").val(JSON.stringify(jsonArray));
   		$("#export_form").submit();
 	}); 
}
/**
 * 下载文件
 */
function downloadFile(){
	//重写该方法，使用虚拟form提交数据，使用post，避免使用a便签。
	$("#btnSaveAs").click(function(){ 
   		var content1 = $("#code_area").val();
   		 if(isNull(content1)){
 			 $.messager.alert('提示信息','代码结果为空，不能保存到文件','error');
 			 return false;
 		 }
   		var content2=encodeURIComponent(content1);
   		$("#content").val(content1);
   		$("#download_form").submit();
 	});
}

function uploadFile(){
	//点击上传按钮，上传文件
	$("#upload").click(function(){
		 var formData = new FormData($("#file_upload_id" )[0]); 
		$.ajax({
			type : 'post',
			url : _path+"/fileUpload.es",//post上传文件
			datatpye : "json",
			 async: false,  
	         cache: false,  
	         contentType: false,  
	         processData: false, 
			data : formData,
			success : function(msgdata) {
				
				if(msgdata.indexOf('您选择的文件不正确，请重新选择') ==-1){
					//var inportJsonArray = new Array(msgdata);//new JSONArray(str)转为json数组
					var inportJsonArray = eval(msgdata);//转为json，去掉前后引号
					//遍历inportJsonArray，把每个jsonObject放入jsonArray中。
					  for (var i =0 ;i<inportJsonArray.length;i++){
						  //判断导入的表名和列名是否已有的json中含有，让客户自己选择
						  //判断有没有这个对象，如果有，让客户自己选择是否覆盖。
						  if(inportJsonArray[i].tableName !=''){
					 		var isExistObj=findObjectInArray(jsonArray, inportJsonArray[i].columnName, inportJsonArray[i].tableName);
					 		if (isExistObj ) {
					 			/* $.messager.confirm('确认对话框', '系统检测到导入的'+inportJsonArray[i].tableName+'表'+inportJsonArray[i].columnName+'列和你已手动设置的重复，是否覆盖？', function(r){
									if (r){
										//先移除，再加新的 */
										jsonArray.splice($.inArray(isExistObj,jsonArray), 1);//移除指定索引；$.inArray(ifExistObj,jsonArray)返回当前obj的索引
										jsonArray.push(inportJsonArray[i]);
								/* 	}else{
										
									}
								}); */
					 			
					 		}else{
					 			jsonArray.push(inportJsonArray[i]);
					 		}
						  }
						
					}  
					
					console.log(jsonArray);
					$('#upload_win').window('close');
				}else{
					$.messager.alert('提示信息','您选择的文件不正确，请重新选择','error');
				}
			}
		});
	});
	
}
/**
 * 查看代码
 */
function queryCode(){
	$("#opencode").click(function(){
		 var codeContent =$("#code_area").val();
		 if(isNull(codeContent)){
			 $.messager.alert('提示信息','代码结果为空，不能查看','error');
			 return false;
		 }
		$.messager.progress({
				title:"提示框",
				text:"正在打开，请稍候..."
				}); 
		 $("#code_win").window('open');
		 $.messager.progress('close');
		$('.CodeMirror').remove();//加载时，把之前的去掉。
		 $("#open_code_area1").val(codeContent);
			//高亮部分，使用的查件。参考使用说明：http://justcoding.iteye.com/blog/2299166；codemirror.css文件可自定义修改样式 http://www.cnblogs.com/oldphper/p/4065425.html
				
			 editor = CodeMirror.fromTextArea(document.getElementById("open_code_area1"), {
		        lineNumbers: true,
		        mode: "text/x-plsql",
		      //代码折叠
		       /*  lineWrapping:true,
		       	foldGutter: true,
		        gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter"], */
		      //括号匹配
		        matchBrackets:true,
		        styleActiveLine: true,
		      	//  智能提示 
		    	// extraKeys:{"Ctrl":"autocomplete"}//ctrl-space唤起智能提示
		       // theme:"eclipse"
		        //extraKeys: {"Ctrl-Space": function(cm) {CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);}}
		    });
		  editor.setSize('100%', '92%');//设置CodeMirror尺寸
		 // editor.setOption('lineWrapping', true);//设置CodeMirror自动换行
		    $(".CodeMirror-scroll").hover(function(){
		        $(this).get(0).style.cursor = "text";
		    });
		    editor.on('change', function() {  
               editor.showHint(); //满足自动触发自动联想功能  
           }); 
		  

	}) 
}
/**
 * 在页面上更新代码
 */
function updateCode(){
	  //点击更新按钮，textarea里面的代码更新到代码结果里去。
	 $("#updateCode1").click(function(){
//		 var editor;//定义编辑器
//		 editor = CodeMirror.fromTextArea(document.getElementById("open_code_area1");
		 var value=editor.getValue();//使用此方法获取编辑器的内容
//		var value= $("#open_code_area1").val();
		$("#code_area").val(value);
		$.messager.alert('提示信息','更新成功','info');
		return false;
	 });
}
/**
 * 根据是否关联显示对应控件
 * @param isManualCorrelation
 */
function dispalyBygl(isManualCorrelation){
	if(isManualCorrelation){
		
		$("#manualCorrelationText").css("color","black");
		$("#manualCorrelation_td").show();
		$("#secend_panel").hide();
		$("#preview").linkbutton({disabled:true}); 
		$("#preview").off("click");
		$("#proportion_tr").show();
		
		//$(this).css("margin-left",'65px');
	}else{
		$("#manualCorrelationText").css("color","#9d9d9d");
		$("#manualCorrelation_td").hide();
		$("#secend_panel").show();
		 $("#preview").linkbutton({disabled:false}); 
		$("#preview").on('click');
		$("#proportion_tr").hide();
	}
}
//获取预制类型的值，隐藏和显示对应的控件。
function isDisplay(userDataType,maxLength){
	if (userDataType == "数字" || userDataType == "number") {
		$("#lengthTd").hide();
		$("#divTd").hide();
		$("#sqlTd").hide();
		$("#dateTd").hide();
		$("#noTd").show();
	}else if(userDataType=="自定义字符" || userDataType == "divChar") {
		 $("#lengthTd").hide();
		 $("#noTd").hide();
		 $("#sqlTd").hide();
		 $("#dateTd").hide();
		 $("#divTd").show();
	}else if(userDataType=="SQL语句" || userDataType == "sql"){
		 $("#lengthTd").hide();
		 $("#noTd").hide();
		 $("#divTd").hide();
		 $("#dateTd").hide();
		 $("#sqlTd").show();
	}else if(userDataType=="日期" ||userDataType == "date" ){
		$("#divTd").hide();
		 $("#noTd").hide();
		 $("#sqlTd").hide();
		 $("#lengthTd").hide();
		 $("#dateTd").show();
		 $("#startDate").datebox('setValue','2010-01-01');//// 设置日期输入框的值
		$("#endDate").datebox('setValue',new Date().getFullYear()+'-'+(new Date().getMonth()+1)+'-'+new Date().getDate());
	}else if(userDataType=="身份证" || userDataType == "idCard"){
		$("#divTd").hide();
		$("#noTd").hide();
		$("#sqlTd").hide();
		$("#dateTd").hide();
		$("#lengthTd").show();
		$("#contain_div").hide();
		$("#startLength").val("18");
		$("#endLength").val("18");
	}else if(userDataType=="电话" ||userDataType=="tel" ){
		$("#divTd").hide();
		$("#noTd").hide();
		$("#sqlTd").hide();
		$("#dateTd").hide();
		$("#lengthTd").show();
		 $("#contain_div").hide();
		$("#startLength").val("11");
		$("#endLength").val("11");
	}else{
		 $("#divTd").hide();
		 $("#noTd").hide();
		 $("#sqlTd").hide();
		 $("#dateTd").hide();
		 $("#lengthTd").show();
		 if(userDataType=="UUID" || userDataType=="uuid"){
			// $("#contain_div").show();
			$("#startLength").val("32");
			$("#endLength").val("32");
			 $("#contain_div").css("display","inline");//修改css样式方法
		 }else{
			 $("#contain_div").hide();
			 $("#startLength").val("1");
			$("#endLength").val(maxLength);
		 }
		 
	}
}
//js表单验证
function verifyDb(){
	var bl = false;
	var reIp=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/g; //IP地址的正则表达式
	var dbServerIp=$("#dbServerIp").val();//IP地址
	var port=$("#dbServerPort").val();//通信端口
	var userName=$("#dbUserName").val();//用户名
	var pwd=$("#dbPassword").val();//密码
	var dbName=$("#dbName").val();//数据库名
	//IP地址合法验证
	if(reIp.test(dbServerIp)){
		if(!RegExp.$1 <256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256){
			bl= true;
		}else{
			layer.alert('请输入正确的IP地址！', -1);
			return false;
		}
	}else{
		layer.alert('请输入正确的IP地址！', -1);
		return false;
	}
	var regu = /^[0-9]+$/;//正整数的正则表达式 
	//通信端口
	if(regu.test(port) && port<65536 && port >1){
		bl= true; 
	}else{
		layer.alert('通信端口，请输入整数并且在1-65535之间！', -1);
		return false;
	}
	if(isNull(userName)){
		layer.alert('用户名不能为空！', -1);
		return false;
	}
	/* if(isNull(pwd)){
		layer.alert('密码不能为空！', -1);
		return false;
	} */
	if(isNull(dbName)){
		layer.alert('数据库名不能为空！', -1);
		return false;
	}
	//用户名
	if(isChina(userName)){
		layer.alert('用户名不能为汉字！', -1);
		return false;
	}
	//密码
	if(isChina(pwd)){
		layer.alert('密码不能为汉字！', -1);
		return false;
	}
	//数据库名
	if(isChina(dbName)){
		layer.alert('数据库名不能为汉字！', -1);
		return false;
	}
	return bl;
}  
//左移数据
function moveToLeft()
{
	var rows = $('#checkedTableList').datagrid('getChecked');// 获取所有选中的行
	if(rows.length==0)
	{  
		$.messager.alert('错误',"请至少选择一行数据!",'error');
		return false;  
	} 
	for (var i = 0; i < rows.length; i++) 
	{  
	     //获取自定义table 的中的checkbox值  
	     var tableName=rows[i].tableName;   
	     $('#tableInfoList').datagrid('insertRow',{
			 	index: 0,	// 索引从0开始
			 	row: {
			 		tableName: tableName
			 	}
			 }); 
   var rowIndex =$("#checkedTableList").datagrid("getRowIndex",rows[i]);//得到索引
	     $("#checkedTableList").datagrid("deleteRow",rowIndex);  
	     
	     
	 }
}
//点击右移按钮，发生数据变化
function moveToRight()
{
	var rows = $('#tableInfoList').datagrid('getChecked');// 获取所有选中的行
	if(rows.length==0)
	{  
		$.messager.alert('错误',"请至少选择一行数据!",'error');
		return false;  
	} 
	
	for (var i = 0; i < rows.length; i++) 
	{  
	     //获取自定义table 的中的checkbox值  
	     var tableName=rows[i].tableName;   
	     $('#checkedTableList').datagrid('insertRow',{
			 //	index: 0,	// 索引从0开始  从0开始，就是放在前面，如果不设置，就在后面追加
			 	row: {
			 		tableName: tableName
			 	}
			 }); 
   var rowIndex =$("#tableInfoList").datagrid("getRowIndex",rows[i]);//得到索引
	     $("#tableInfoList").datagrid("deleteRow",rowIndex);  
	     
	     
	 }
	
}
function findObjectInArray(array,nodeText,parentNodeText){
	for ( var i = 0; i < array.length; i++) {
		//alert(array[i].columnName+";"+array[i].tableName);
        if (nodeText == array[i].columnName && parentNodeText == array[i].tableName) {
        	return array[i];
        }
    }
	 return false;
}



//重置日期控件格式
function myformatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}
function myparser(s){
	if (!s) return new Date();
	var ss = (s.split('-'));
	var y = parseInt(ss[0],10);
	var m = parseInt(ss[1],10);
	var d = parseInt(ss[2],10);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
		return new Date(y,m-1,d);
	} else {
		return new Date();
	}
}


function preview(){

//$("#preview").click(function(){

	var userDataType_2=$("#userDataType").combobox('getText')//预置类型
	var startNo_2=$("#startNo").val();//数值开始
	var endNo_2=$("#endNo").val();//数值结束
	var startLength_2=$("#startLength").val();//长度开始
	var endLength_2=$("#endLength").val();//长度结束
	var startDate_2=$('#startDate').datebox('getValue');//开始日期
	var endDate_2=$('#endDate').datebox('getValue');//结束日期
	var diy_2= $.trim($("#diy").val());//自定义字符
	var sql_2= $.trim($("#sql").val());//SQL语句
	var isContain_=$("#isContain_").is(":checked");//判断是否勾选了包含-
	var isConnect=$("#isConnect").is(":checked");//判断是否勾选了包含-
	
	//不管删不删除以前的，都要添加当前页面数据。
	if (userDataType_2 == "数字") {	
		if(isNull(startNo_2) || isNull(endNo_2)){
			layer.alert('不能为空！', -1);
			return false;
		}else {
			if(!isNumber(startNo_2) || (!isNumber(endNo_2))){
				layer.alert('长度必须为数字！', -1);
				return false;
			}
		}
		if ($("#paramsType").combobox('getText')=='随机小数' && !isNumber($.trim($("#numdigits").val()))) {
			layer.alert('保留小数位数必须为小于7的数字！', -1);
			return false;
		}
		}else if(userDataType_2=="自定义字符" ) {
			if(isNull(diy_2)){
				layer.alert('不能为空！', -1);
				return false;
			}	
		}else if(userDataType_2=="SQL语句"){
			if(isNull(sql_2)){
				layer.alert('不能为空！', -1);
				return false;
			}	
		}else if(userDataType_2=="日期"){
			if(isNull(startDate_2) || isNull(endDate_2)){
				layer.alert('不能为空！', -1);
				return false;
			}
		}else {
			if(isNull(startLength_2) || isNull(endLength_2)){
				layer.alert('不能为空！', -1);
				return false;
			}else {
				if(!isNumber(startLength_2) || (!isNumber(endLength_2))){
					layer.alert('长度必须为数字！', -1);
					return false;
				}
			}
		
	} 
	if (!isNull($("#isFormatMessage").html())) {
		layer.alert('参数格式不符合规则', -1);
		return false;
	}
/*  	$.messager.progress({
		title:"提示框",
		text:"正在计算，请稍候..."
		});  */
 	
	$.ajax({
				type : 'post',
				url : _path+"/ColumnControl/preView",//测试连接的方法
				datatpye : "script/html",
				data : $("#columnInfo").serialize(),//serialize() 方法通过序列化表单值，创建 URL 编码文本字符串。例如：username=admin&password=admin123
				success : function(msgdata) {
					$.messager.progress('close');
					//if()
					//$.messager.alert('预览结果',msgdata,'info'); 
					if( JSON.stringify(msgdata).indexOf('Exception:') !=-1){
						$.messager.alert('预览结果',msgdata,'error'); 
					}else{
						$.messager.alert('测试结果',msgdata,'info'); 
					}
				}				
			});
//})
}

/** 
 *点击导入配置
 */  
function openUpload(){  
	 if(!$("#conn").linkbutton('options').disabled)//获取该按钮的启用、禁用状态
		{
			 $.messager.alert('提示信息','请先连接数据库并正确选择待操作的数据表！','error');
			 return false;
		}
	 $('#upload_win').window('open');

}
//在选择表时，提供搜索功能
function searchTable(){
	var bl= false;
	var regu = /^\..*$/;//正整数的正则表达式 
	//通信端口
	if(!regu.test($("#searchTableName").attr("value")) ){
		bl= true; 
	}else{
		layer.alert('输入格式错误', -1);
		return false;
	}
	$('#tableInfoList').datagrid({
	 	 title:'', 
	 	 method:$("#db_form").attr("method"),
	     url:_path+"/dbControl/searchTableName", 
	     queryParams:{
				searchTableName:$("#searchTableName").attr("value")
	        },
        striped : true,//设置为true将交替显示行背景。

        loadMsg : '数据装载中......',
        rownumbers: true,
        singleSelect: false,
		selectOnCheck: true,
		checkOnSelect: true,
         columns:[[
					{field:'checkbox',checkbox:true },//显示复选框样式，单独列出来一列，json返回内容不需包括
					{field:'tableName',title:'Table' }
					]],
        	 //onLoadSuccess:function()，判断返回值，但返回值必须是json格式。并且是{'rows':[{'tableName':'连接不成功'}]}这种格式
       /*  onLoadSuccess:function(data){ //JSON.stringify(data) 方法把data转为字符串，str.indexOf(str2)!=-1;判断是否包含某字符串
        	$.messager.progress('close');
        	 if(JSON.stringify(data).indexOf("连接不成功") != -1){
        		$.messager.alert('连接结果',"连接不成功",'error');
        	}else {
        		$('#win').window('open');

        	} 
        } */
    });	 
}
function paramFormatToDemo(src){
	var regu = "^%(0[0-9]{1,2})d$";   
	var re = new RegExp(regu); 
	var	isFormat=re.test(src);
    return isFormat;
}

function join(length){
	var value="1";
	for (var i=1;i<length;i++)
	{
		value="0"+value;
	}
	return value;
}
/**
 * 通过参数格式动态变化示例值
 * @returns {Boolean}
 */
function demoValueChangeByFormat(){
	var FormatType = $("#paramsFormat").combobox('getText');
	//判断是否符合格式
	var isFormat=paramFormatToDemo(FormatType);
	if(!isFormat){
		$("#isFormatMessage").html("你手动输入的不符合格式规则，请填写%0xxd类似的规则");
			return false;
	}else{
		$("#isFormatMessage").html("");
		var formatLength=FormatType.substring(2,4);
		if (formatLength.indexOf("d")!=-1) {
			formatLength=formatLength.substring(0,1);
		}
		var formatLengthInt = parseInt(formatLength);
		var demoValue = join(formatLengthInt);
		$("#paramsDemo").val(demoValue);
	}
	
}