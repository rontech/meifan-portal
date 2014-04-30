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
                 }
            	 
            	 
             }else{
            	 $('#stylistSerchMsg').text("很抱歉，检测失败，请稍候重试");
             }
         }else{
        	 $('#stylistSerchMsg').text("正在查询");
         }
     };
     xhr.send(null);
    
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
            	 $('#salonSerchMsg').text("很抱歉，检测失败，请稍候重试").removeClass("trueMsg").addClass("errorMsg");
             }
         }else{
        	 $('#salonSerchMsg').text("正在查询");
         }
     };
     xhr.send(null);
    
}

$('#SalonAccountId').focus(function(){
    $('#SalonAccountId').next().text("请输入您所属店铺的店铺ID").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
	checkSalonAccountId()
});

function checkSalonAccountId(){
	var value = $('#SalonAccountId').val();
    if (value == ""){
    	$('#applySerchSalonMsg').text("店铺ID不能为空").removeClass("trueMsg").addClass("errorMsg");
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
                	 $('#applySerchSalonMsg').text("查找不到此店铺，确认无误后，请重新输入").removeClass("trueMsg").addClass("errorMsg");
                 }
            	 
            	 
             }else{
            	 $('applySerchSalonMsg').text("很抱歉，检测失败，请稍候重试");
             }
         }else{
        	 $('#applySerchSalonMsgg').text("正在查询");
         }
     };
     xhr.send(null);
}

$('#WorkYears').focus(function(){
    $('#WorkYears  ~ .help-inline').text("工作年数请取整").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
	checkWorkYears();
});

function checkWorkYears(){
    var value = $('#WorkYears').val();
	var isValid =/^([1-9]|[1-9][0-9])$/;
	if(value ==''){
		$('#WorkYears  ~ .help-inline').text('工作年数不能为空，请输入').removeClass("trueMsg").addClass("errorMsg");
		return;
	}
	if(!isValid.test(value)){
		$('#WorkYears  ~ .help-inline').text('格式不正确，请重新输入').removeClass("trueMsg").addClass("errorMsg");
	}else{
		$('#WorkYears  ~ .help-inline').text('').removeClass("errorMsg").addClass("trueMsg");
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
				$(this).next().text('请选择行业').removeClass("trueMsg").addClass("errorMsg");
			}
		}
		
	});
	var $Position = $('.PositionName');
	$Position.each(function(i){
		if(i==($Position.length-1)){
			
		}else{
			if($Position.eq(i).val()==''){
				$(this).next().text('请选择职位').removeClass("trueMsg").addClass("errorMsg");
			}
		}
		
	});
	var errInput = $('.errorMsg');
    if (errInput.length != 0 ){
        return false;
    }
    document.stylistForm.submit();
}

function checkPositionName(obj){
	if(obj.value == ""){
		$(obj).next().text('请选择职位').removeClass("trueMsg").addClass("errorMsg");
	}else{
		$(obj).next().text('').removeClass("errorMsg").addClass("trueMsg");
	}
}

function checkIndustryName(obj){
	if(obj.value == ""){
		$(obj).next().text('请选择行业').removeClass("trueMsg").addClass("errorMsg");
	}else{
		$(obj).next().text('').removeClass("errorMsg").addClass("trueMsg");
	}
}