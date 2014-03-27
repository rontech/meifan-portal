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