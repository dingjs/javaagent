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
