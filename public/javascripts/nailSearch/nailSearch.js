
$(function() {
	$('.serviceType_radio').click(function(){
		submitForm();
	});

    $('.styleColor_checkbox').click(function(){
        submitForm();
    });

    $('.styleMaterial_checkbox').click(function(){
        submitForm();
    });

    $('.styleBase_checkbox').click(function(){
        submitForm();
    });

    $('.styleImpression_checkbox').click(function(){
        submitForm();
    });

    $('.socialScene_checkbox').click(function(){
        submitForm();
    });

    $('.stylistId_seclect').click(function(){
        submitForm();
    });

    //画面联动，由技术类别的修改显示字段不同
//    $('.serviceType_seclect').click(function(){
//        alert("我的 的 b");
//        changeField();
//    });

});

function submitForm(){
    document.getElementById("nailSearchForm").submit();
}

//画面联动，由技术类别的修改显示字段不同 st=serviceType
function changeField(st){
  if(st == 'Nail'){
    document.getElementById("nail_styleImpression").style.display="table-row";
    document.getElementById("nail_styleColor").style.display="table-row";
    document.getElementById("nail_styleBase").style.display="table-row";
    document.getElementById("nail_styleMaterial").style.display="table-row";
    document.getElementById("nail_socialScene").style.display="table-row";
  }
  if(st == 'HandCare'){
    document.getElementById("nail_styleImpression").style.display="none";
    document.getElementById("nail_styleColor").style.display="none";
    document.getElementById("nail_styleBase").style.display="none";
    document.getElementById("nail_styleMaterial").style.display="none";
    document.getElementById("nail_socialScene").style.display="none";

  }
  if(st == 'Eyelashes'){
    document.getElementById("nail_styleImpression").style.display="none";
    document.getElementById("nail_styleColor").style.display="none";
    document.getElementById("nail_styleBase").style.display="none";
    document.getElementById("nail_styleMaterial").style.display="none";
    document.getElementById("nail_socialScene").style.display="none";
  }
}