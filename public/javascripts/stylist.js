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
	var xhr = getXmlHttpRequest();
	xhr.open('get',
    '/mySalon/checkStylistIsExist/'+userId,
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
                	 $('#stylistSerchMsg').text("没有此技师");
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
                	 $('#salonSerchMsg').text("没有此店铺");
                 }
            	 
            	 
             }else{
            	 $('#salonSerchMsg').text("发生错误");
             }
         }else{
        	 $('#salonSerchMsg').text("正在查询");
         }
     };
     xhr.send(null);
    
}

$('#applyStylistFindSalon').blur(function(){
	var salonId = $(this).val();
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
                	 $('#applySerchSalonMsg').text("可以申请");
                 }
                 if(txt == 'NO'){
                	 $('#applySerchSalonMsg').text("没有此店铺");
                 }
            	 
            	 
             }else{
            	 $('applySerchSalonMsg').text("发生错误");
             }
         }else{
        	 $('#applySerchSalonMsgg').text("正在查询");
         }
     };
     xhr.send(null);
});
	
