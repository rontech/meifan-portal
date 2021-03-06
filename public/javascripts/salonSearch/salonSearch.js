$(function() {
    $('.select_region').click(function(){
        submitForm();
    });
    
    $('.seatNums_MinNum').click(function(){
        var minSeatObj=$('input:radio[name="seatNums.minNum"]:checked');
        minSeatObj.next().attr("checked",'checked');
     submitForm();
    });

    $('.priceRang_minPrice').click(function(){
        var minPriceObj=$('input:radio[name="priceRange.minPrice"]:checked');
        minPriceObj.next().attr("checked",'checked');
        submitForm();
    });

    $('#surePrice').click(function(){
        var minPrice = $('#lowPrice').val();
        var maxPrice = $('#highPrice').val();
        var re=/^[1-9]([0-9])*|0$/;
        if(maxPrice == ''){
        	if(re.test(minPrice)){
        		
        		$('.fill_priceRang_minPrice').val(minPrice);
                $('.fill_priceRang_minPrice').attr("checked",'checked');
        		$('.fill_priceRang_maxPrice').val(99999999);
        		$('.fill_priceRang_maxPrice').attr("checked",'checked');
        		submitForm();
        	}
        	return;
        }
        if(re.test(maxPrice) && minPrice == ''){
            $('.fill_priceRang_minPrice').val(0);
            $('.fill_priceRang_minPrice').attr("checked",'checked');
            $('.fill_priceRang_maxPrice').val(maxPrice);
            $('.fill_priceRang_maxPrice').attr("checked",'checked');
            submitForm();
            return;
        }
        if(!re.test(maxPrice) || !re.test(minPrice)){
        	
        	return;
        }
        if(Number(minPrice) > Number(maxPrice)) {
        	
        	return;
        }
        
        
        $('.fill_priceRang_minPrice').val(minPrice);
        //$('input[name="priceRange.minPrice"]:eq(0)').attr("checked",'checked');
        $('.fill_priceRang_minPrice').attr("checked",'checked');
        $('.fill_priceRang_maxPrice').val(maxPrice);
        $('.fill_priceRang_maxPrice').attr("checked",'checked');
        submitForm();
    });

    $('#unlimitSeatNum').click(function(){
        $('input[name="seatNums.minNum"]:eq(0)').attr("checked",'checked');
        $('input[name="seatNums.maxNum"]:eq(0)').attr("checked",'checked');
        submitForm();
    });

    $('#unlimitPrices').click(function(){
        $('input[name="priceRange.minPrice"]:eq(0)').attr("checked",'checked');
        $('input[name="priceRange.maxPrice"]:eq(0)').attr("checked",'checked');
        submitForm();
    });

    $('.serviceType_checkbox').click(function(){
        submitForm();
    });

    $('.serviceType_radio').click(function(){
        submitForm();
    });

    $('.brand_checkbox').click(function(){
        submitForm();
    });

    $('.condtions_item').click(function(){
        submitForm();
    });

    $('.styleMaterial_checkbox').click(function(){
        submitForm();
    })

    $('.styleBase_checkbox').click(function(){
        submitForm();
    })

    $('.styleImpression_checkbox').click(function(){
        submitForm();
    })

    $('.socialScene_checkbox').click(function(){
        submitForm();
    })

    // Sort by price.
    $('#price').click(function() {
        $('#selSortKey').val('price');
        submitForm();
   })

    // Sort by popularity.
    $('#popu').click(function() {
        // TODO
        //alert($('#popu').prop('checked'));
        //if(!$('#popu').prop('checked')) {
            $('#selSortKey').val('popu');
            submitForm();
        //}
    })

    // Sort by review.
    $('#review').click(function() {
        // TODO
        //if(!$('#review').prop('checked')) {
            $('#selSortKey').val('review');
            submitForm();
        //}
    })
    
});


/**
 * Function to get checkbox values in a group.
 */
function setChkBoxValueInGrp(grpId, inputTag) {
    var checkArray = document.getElementById(grpId).getElementsByTagName(inputTag);
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
        }
    }
}


function unlimitedCondtions(){
    var checkArray = document.getElementById("otherCondtion_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
            }
    }
    submitForm();
    
}


function unlimitSalons(){
    var checkArray = document.getElementById("unlimitSalons_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
            }
    }
    submitForm();
}

function unlimitStyleMaterial(){
    var checkArray = document.getElementById("unlimitStyleMaterial_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
        }
    }
    submitForm();
}

function unlimitStyleBase(){
    var checkArray = document.getElementById("unlimitStyleBase_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
        }
    }
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

function unlimitImpression(){
    var checkArray = document.getElementById("unlimitImpression_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
        }
    }
    submitForm();
}

function unlimitSocialScene(){
    var checkArray = document.getElementById("unlimitSocialScene_group").getElementsByTagName("input");
    for(var i=0; i<checkArray.length; i++){
        if(checkArray[i].type=='checkbox'){
            checkArray[i].checked=false;
        }
    }
    submitForm();
}

function unlimitedRegion(){
    document.getElementById("unlimitedRegion").checked=true;
    $('#areafield').empty();
    submitForm();
}

function submitForm(){
	if($('#getcity_name').val()=='中文/拼音'){
		$('.com_city_bug').css('display', 'inline');
		return;
	}
    document.getElementById("salonSearchForm").submit();
}

function clickSalonSearch(){
	submitForm();
}


/*------------------------------
 * Highlight search result.
 *------------------------------*/
$(document).ready(function() {
    //highlight($('#keyword').val());
});

function encode(s){
    return s.replace(/&/g,"&").replace(/</g,"<").replace(/>/g,">").replace(/([\\\.\*\[\]\(\)\$\^])/g,"\\$1");
}

function decode(s){
    return s.replace(/\\([\\\.\*\[\]\(\)\$\^])/g,"$1").replace(/>/g,">").replace(/</g,"<").replace(/&/g,"&");
}

/**
 * TODO for muliti keyword split by blank.
 */
function highlight(src){
    var s=src.trim()
    if (s.length==0) { return false; }
 
    s=encode(s);
    // Highlight the keyword hit result.
    var objGrp=document.getElementsByName("keywordHitResult");
    for(var i=0;i<objGrp.length;i++) {
        var obj=objGrp[i];
        var t=obj.innerHTML.replace(/<span\s+class=.?highlightFw.?>([^<>]*)<\/span>/gi,"$1");
        obj.innerHTML=t;
        var cnt=loopSearch(s,obj);
        t=obj.innerHTML
        var r=/{searchHL}(({(?!\/searchHL})|[^{])*){\/searchHL}/g
        t=t.replace(r,"<span class='highlightFw'>$1</span>");
        obj.innerHTML=t;
    }
}

function loopSearch(s,obj){
    var cnt=0;
    if (obj.nodeType==3){
        cnt=replace(s,obj);
        return cnt;
    }
    for (var i=0,c;c=obj.childNodes[i];i++){
        if (!c.className||c.className!="highlightFw")
            cnt+=loopSearch(s,c);
    }
    return cnt;
}

function replace(s,dest){
    var r=new RegExp(s,"g");
    var tm=null;
    var t=dest.nodeValue;
    var cnt=0;
    if (tm=t.match(r)){
        cnt=tm.length;
        t=t.replace(r,"{searchHL}"+decode(s)+"{/searchHL}")
        dest.nodeValue=t;
    }
    return cnt;
}

/**
 * TODO for auto show form condition.
 */

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

function checkHideOption(){
	 var salonServiceType_hide = $('.salonServiceType_hide');
	 salonServiceType_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.salonServiceType_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });
	    
	 var salonSalonName_hide = $('.salonSalonName_hide');
	 salonSalonName_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.salonSalonName_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });  

	 var salonPriceRandge_hide = $('.salonPriceRandge_hide');
	 salonPriceRandge_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.salonPriceRandge_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });

	 var salonOtherCon_hide = $('.salonOtherCon_hide');
	 salonOtherCon_hide.each(function(i){
	    	if($(this).children(':checked').length>0){
	    		$('.salonOtherCon_hide').removeClass('hide_li').addClass('dibBL');
	    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
	    		$(this).parents('.rBox').prev().prev().css('display','block');
	    		return;
	    	}
	    });
	 var minPrice = $('#lowPrice').val();
     var maxPrice = $('#highPrice').val();
     if(minPrice != '' || maxPrice !=''){
    	 $('.salonPriceRandge_hide').removeClass('hide_li').addClass('dibBL');
    		$(this).parents('.rBox').prev().prev().prev().css('display','none');
    		$(this).parents('.rBox').prev().prev().css('display','block');
    		return;
	 }        
}
$(document).ready(function() {
	setTimeout('checkHideOption()', 600);
    
});
