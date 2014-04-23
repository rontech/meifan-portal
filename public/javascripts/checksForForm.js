/**
 * Created by YS-HAN on 14/04/23.
 */
var ITEM_TYPE_ID = "loginId"
var ITEM_TYPE_NAME = "name"
var ITEM_TYPE_EMAIL = "email"
var ITEM_TYPE_TEL = "tel"

$('#salonAccountId').blur(function(){
    var salonId = $('#salonAccountId').val();
    var isName=/^[a-zA-Z][a-zA-Z0-9_]{5,17}$/;
    if (salonId == ""){
        $('#salonAccountId  ~ .help-inline').text('店铺ID不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isName.test(salonId)){
        $('#salonAccountId  ~ .help-inline').text('该店铺ID不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.Application.itemIsExist(salonId, ITEM_TYPE_ID).ajax({
        /*data: formData,
        processData: false,
        contentType: false,*/
        cache: false,
        type: 'GET',
        success: function(data){
            if (data == "false"){
                $('#salonAccountId  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#salonAccountId  ~ .help-inline').text("该店铺ID已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }

        },
        error: function(err){
            alert(err.status+"出现异常错误");
        }
    });
});

$('#password_main').blur(function(){
    var password=$(this).val();
    var isPassword=/^[a-zA-Z0-9]\w{5,17}$/;
    if (password == ""){
        $('#password_main  ~ .help-inline').text('密码不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isPassword.test(password)){
        $("#password_main ~ .help-inline").text("密码不合法").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    $("#password_main ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
});
$('#password_confirm').blur(function(){
    var password=$(this).val();
    var firstPassword=$("#password_main").val();
    if(password!=firstPassword){
        $("#password_confirm ~ .help-inline").text("两次输入的密码不一致，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    $("#password_confirm ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
});

$('#contact_email').blur(function(){
    var email=$(this).val();
    var isEmail=/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/;
    if(!isEmail.test(email)){
        $("#contact_email ~ .help-inline").text("邮箱无效，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact_email ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }

});
$('#contact_tel').blur(function(){
    var tel=$(this).val();
    var isTel=/(^(13|14|15|18)\d{9}$)/;
    if(!isTel.test(tel)){
        $("#contact_tel ~ .help-inline").text("该号码无效，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact_tel ~ .help-inline").removeClass("errorMsg").addClass("trueMsg");
    }
});