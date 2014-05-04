	function unlimitStyleLength(){
		$('input[name="styleLength"]:eq(0)').attr('checked','checked');
		submitForm();
	}

	function unlimitStyleImpression(){
		$('input[name="styleImpression"]:eq(0)').attr('checked','checked');
		submitForm();
	}
	
	function unlimitServiceType(){
		var checkArray = document.getElementById("unlimitServiceType_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitFaceShape(){
		var checkArray = document.getElementById("unlimitFaceShape_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitConsumerSex(){
		$('input[name="consumerSex"]:eq(0)').attr('checked','checked');
		submitForm();
	}

	function unlimitStyleColor(){
		var checkArray = document.getElementById("unlimitStyleColor_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitConsumerSocialStatus(){
		var checkArray = document.getElementById("unlimitConsumerSocialStatus_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitConsumerAgeGroup(){
		var checkArray = document.getElementById("unlimitConsumerAgeGroup_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitStyleAmount(){
		var checkArray = document.getElementById("unlimitStyleAmount_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitStyleQuality(){
		var checkArray = document.getElementById("unlimitStyleQuality_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function unlimitStyleDiameter(){
		var checkArray = document.getElementById("unlimitStyleDiameter_group").getElementsByTagName("input");
	    for(var i=0; i<checkArray.length; i++){
	        if(checkArray[i].type=='checkbox'){
	            checkArray[i].checked=false;
	            }
	    }
	    submitForm();
	}

	function moreProp(obj){
		obj.style.display='none';
		$('#moreSearchOption').css('display','block');
		$('#lessProp').css('display','block');
	}
	function lessProp(obj){
		obj.style.display='none';
		$('#moreSearchOption').css('display','none');
		$('#moreProp').css('display','block');
	}
	function submitForm(){
		document.getElementById("styleSearchForm").submit();
	}
	
	function moreExpandValue(obj,type){
		var el = $(obj)
		el.css('display','none');
		el.next().css('display','block');
		var txt = '.'+type;
		$(txt).removeClass('hide_li').addClass('dibBL');
	}

	function lessExpandValue(obj,type){
		var el = $(obj)
		el.css('display','none');
		el.prev().css('display','block');
		var txt = '.'+type;
		$(txt).removeClass('dibBL').addClass('hide_li');
	}
	
	function showOption(){
		$('#moreProp').css('display','none');
		$('#moreSearchOption').css('display','block');
		$('#lessProp').css('display','block');
	}
	$(document).ready(function() {
		setTimeout('checkHideOption()', 600);
	    
	});
	function checkHideOption(){
		var checkArrayStyleColor = document.getElementById("unlimitStyleColor_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayStyleColor.length; i++){
	        if(checkArrayStyleColor[i].type=='checkbox'){
	        	if(checkArrayStyleColor[i].checked == true){
	        	showOption();
	        	return;
	            }
	        }	
	    }

	    var checkArrayConsumerSocialStatus = document.getElementById("unlimitConsumerSocialStatus_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayConsumerSocialStatus.length; i++){
	        if(checkArrayConsumerSocialStatus[i].type=='checkbox'){
	        	if(checkArrayConsumerSocialStatus[i].checked == true){
	        	showOption();
	        	return;
	            }
	        }	
	    }

	    var checkArrayConsumerAgeGroup = document.getElementById("unlimitConsumerAgeGroup_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayConsumerAgeGroup.length; i++){
	        if(checkArrayConsumerAgeGroup[i].type=='checkbox'){
	        	if(checkArrayConsumerAgeGroup[i].checked == true){
	        	showOption();
	        	return;
	            }
	        }	
	    }

	    var checkArrayStyleAmount = document.getElementById("unlimitStyleAmount_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayStyleAmount.length; i++){
	        if(checkArrayStyleAmount[i].type=='checkbox'){
	        	if(checkArrayStyleAmount[i].checked == true){
	        	showOption();
	        	return;
	            }
	        }	
	    }

	    var checkArrayStyleQuality = document.getElementById("unlimitStyleQuality_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayStyleQuality.length; i++){
	        if(checkArrayStyleQuality[i].type=='checkbox'){
	        	if(checkArrayStyleQuality[i].checked == true){
	        	showOption();
	        	return;
	        	}
	        }
	    }

	    var checkArrayStyleDiameter = document.getElementById("unlimitStyleDiameter_group").getElementsByTagName("input");
	    for(var i=0; i<checkArrayStyleDiameter.length; i++){
	        if(checkArrayStyleDiameter[i].type=='checkbox'){
		        if(checkArrayStyleDiameter[i].checked == true){
			        showOption();
		        	return;
			    }
	        	
	        }
	    }
	    
	    var styleImpression_hide = $('.styleImpression_hide');
	    styleImpression_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.styleImpression_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });
	    
	    var serviceType_hide = $('.serviceType_hide');
	    serviceType_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.serviceType_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });
	}
	
$(function() {
	$('.StyleLength_item').click(function(){
		submitForm();
	});

	$('.styleImpression_item').click(function(){
		submitForm();
	});

	$('.serviceType_item').click(function(){
		submitForm();
	});

	$('.faceShape_item').click(function(){
		submitForm();
	});

	$('.consumerSex_item').click(function(){
		submitForm();
	});

	$('.styleColor_item').click(function(){
		submitForm();
	});

	$('.consumerSocialStatus_item').click(function(){
		submitForm();
	});

	$('.consumerAgeGroup_item').click(function(){
		submitForm();
	});

	$('.styleAmount_item').click(function(){
		submitForm();
	});

	$('.styleQuality_item').click(function(){
		submitForm();
	});

	$('.styleDiameter_item').click(function(){
		submitForm();
	});
});