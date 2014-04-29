$(function(){
	$("searchStylistToSubmit").click(function(){
		var stylistId=$(this).val();
		var salonId=$("#stylist-search-salon").val();
		$.get(
				"/salonAdmin/checkStylistIsValid/"+salonId+"/"+stylistId,
				function(data){
					$("#stylistSearchResult").text(data);
					if(data == "抱歉该技师已属于其它店铺" || data == "暂无该技师"){
						return false;
					}
				});
	});
})


function getXmlHttpRequest(){
    var xhr = null;
    if((typeof XMLHttpRequest)!='undefined'){
          xhr = new XMLHttpRequest();
       }else {
          xhr = new ActiveXObject('Microsoft.XMLHttp');
       }
       return xhr;
}

function checkStylistExist(){
	var userId = $('#stylist-search-stylist').val();
	var salonId = $('#stylist-search-salon').val();
	var xhr = getXmlHttpRequest();
	xhr.open('get',
    '/mySalon/checkStylistIsExist/'+userId+'/'+salonId,
    true);
     xhr.onreadystatechange=function(){
         //step4 获取服务器返回的数据，更新页面
         if(xhr.readyState == 4){
             if(xhr.status == 200){
            	 var txt = xhr.responseText;
                 if(txt == 'YES'){
                	 document.getElementById("getStylistForm").submit();
                 }
                 if(txt == 'NO'){
                	 $('#stylistSerchMsg').text("没有发现此技师或该技师已属其它店铺，请重新输入");
                	 $('.searchStylistDisplay').remove();
                	 return false;
                 }
            	 
            	 
             }else{
            	 $('#stylistSerchMsg').text("发生错误");
             }
         }else{
        	 $('#stylistSerchMsg').text("正在查询");
         }
     };
     xhr.send(null);
    
}


function butOnClick() {
	if (event.keyCode == 13) {
		var button = document.getElementById("searchStylistToSubmit");
		button.click();
		return false;
	}
}

function checkSalonExist(){
	var salonId = $('#stylist-search-salon').val();
	var xhr = getXmlHttpRequest();
	xhr.open('get',
    '/myPage/checkSalonIsExit/'+salonId,
    true);
     xhr.onreadystatechange=function(){
         //step4 获取服务器返回的数据，更新页面
         if(xhr.readyState == 4){
             if(xhr.status == 200){
            	 var txt = xhr.responseText;
                 if(txt == 'YES'){
                	 document.getElementById("getSalonForm").submit();
                 }
                 if(txt == 'NO'){
                	 $('#salonSerchMsg').text("没有发现此店铺，确认您输入的店铺ID是否正确").removeClass("trueMsg").addClass("errorMsg");
                 }
            	 
            	 
             }else{
            	 $('#salonSerchMsg').text("发生错误").removeClass("trueMsg").addClass("errorMsg");
             }
         }else{
        	 $('#salonSerchMsg').text("正在查询");
         }
     };
     xhr.send(null);
    
}

$('#SalonAccountId').blur(function(){
	checkSalonAccountId()
});

function checkSalonAccountId(){
	var value = $('#SalonAccountId').val();
    var isValid = /^[a-zA-Z][a-zA-Z0-9_]{5,17}$/;
    if (value == ""){
    	$('#applySerchSalonMsg').text("店铺ID不能为空").removeClass("trueMsg").addClass("errorMsg");
    	return;
    }
    if(!isValid.test(value)){
    	$('#applySerchSalonMsg').text("该ID不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    var xhr = getXmlHttpRequest();
	xhr.open('get',
    '/myPage/checkSalonIsExit/'+value,
    true);
     xhr.onreadystatechange=function(){
         //step4 获取服务器返回的数据，更新页面
         if(xhr.readyState == 4){
             if(xhr.status == 200){
            	 var txt = xhr.responseText;
                 if(txt == 'YES'){
                	 $('#applySerchSalonMsg').text("").removeClass("errorMsg").addClass("trueMsg");
                 }
                 if(txt == 'NO'){
                	 $('#applySerchSalonMsg').text("查找不到此店铺，请重新输入").removeClass("trueMsg").addClass("errorMsg");
                 }
            	 
            	 
             }else{
            	 $('applySerchSalonMsg').text("发生错误");
             }
         }else{
        	 $('#applySerchSalonMsgg').text("正在查询");
         }
     };
     xhr.send(null);
}

$('#WorkYears').blur(function(){
	checkWorkYears();
});

function checkWorkYears(){
    var value = $('#WorkYears').val();
	var isValid =/^([1-9]|[1-9][0-9])$/;
	if(value ==''){
		$('#WorkYears').parent('dd').next().text('年数不能为空，请重新输入').removeClass("trueMsg").addClass("errorMsg");
		return;
	}
	if(!isValid.test(value)){
		$('#WorkYears').parent('dd').next().text('无效的年数，请重新输入').removeClass("trueMsg").addClass("errorMsg");
		return;
	}else{
		$('#WorkYears').parent('dd').next().text('').removeClass("errorMsg").addClass("trueMsg");
	}
}

function checkForApplyStylist(){
	checkWorkYears()
	checkSalonAccountId()
	var $indust = $('.IndustryName');
	$indust.each(function(i){
		if(i==($indust.length-1)){
			
		}else{
			if($indust.eq(i).val()==''){
				$(this).parent('dd').next().text('无效的行业').removeClass("trueMsg").addClass("errorMsg");
			}
		}
		
	});
	var $Position = $('.PositionName');
	$Position.each(function(i){
		if(i==($Position.length-1)){
			
		}else{
			if($Position.eq(i).val()==''){
				$(this).parent('dd').next().text('无效的职位').removeClass("trueMsg").addClass("errorMsg");
			}
		}
		
	});
	var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
	return true;
}

function checkPositionName(obj){
	if(obj.value == ''){
		$(obj).parent('dd').next().text('无效的职位').removeClass("trueMsg").addClass("errorMsg"); 
	}else{
		$(obj).parent('dd').next().text('').removeClass("errorMsg").addClass("trueMsg");
	}
}

function checkIndustryName(obj){
	if(obj.value == ''){
		$(obj).parent('dd').next().text('无效的行业').removeClass("trueMsg").addClass("errorMsg"); 
	}else{
		$(obj).parent('dd').next().text('').removeClass("errorMsg").addClass("trueMsg");
	}
}