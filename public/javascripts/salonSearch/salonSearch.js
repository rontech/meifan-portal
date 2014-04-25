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
        var re=/^[1-9]([0-9])*$/;
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
        if(maxPrice < minPrice) {
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

    $('.brand_checkbox').click(function(){
        submitForm();
    });

    $('.condtions_item').click(function(){
        submitForm();
    });
    
}); 

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

function unlimitServiceType(){
    var checkArray = document.getElementById("unlimitServiceType_group").getElementsByTagName("input");
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
    document.getElementById("salonSearchForm").submit();
}




/*------------------------------
 * Highlight search result.
 *------------------------------*/
$(document).ready(function() {
    highlight($('#keyword').val());
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
