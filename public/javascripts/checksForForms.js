/**
 * Created by YS-HAN on 14/04/23.
 */
var ITEM_TYPE_ID = "loginId"
var ITEM_TYPE_NAME = "name"
var ITEM_TYPE_EMAIL = "email"
var ITEM_TYPE_TEL = "tel"

$('#accountId').blur(function(){
    checkedAccountId();
});
$('#password_main').blur(function(){
    checkedPassword()
});
$('#password_confirm').blur(function(){
    checkedPasswordConfirm()
});
$('#contact_email').blur(function(){
    checkedEmail()
});
$('#contact_tel').blur(function(){
    checkedTel()
});
$('#salonName').blur(function(){
    checkedName()
});
$('#contact').blur(function(){
    checkedContact()
});
$('.salonIndustry').change(function(){
    checkedIndustry()
});

function checkedAccountId(){
    var salonId = $('#accountId').val();
    var isName=/^[a-zA-Z][a-zA-Z0-9_]{5,17}$/;
    if (salonId == ""){
        $('#accountId  ~ .help-inline').text("登录ID不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isName.test(salonId)){
        $('#accountId  ~ .help-inline').text('该登录ID不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.Application.itemIsExist(salonId, ITEM_TYPE_ID).ajax({
        /*data: formData,
         processData: false,
         contentType: false,*/
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#accountId  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#accountId  ~ .help-inline').text("该登录ID已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#accountId  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedPassword(){
    var password=$('#password_main').val();
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
}

function checkedPasswordConfirm(){
    var password=$('#password_confirm').val();
    var firstPassword=$("#password_main").val();
    if(password!=firstPassword){
        if (password == ""){
            $('#password_confirm  ~ .help-inline').text('请再次输入密码').removeClass("trueMsg").addClass("errorMsg");
            return;
        }
        $("#password_confirm ~ .help-inline").text("两次输入的密码不一致，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        if (password == ""){
            $("#password_confirm ~ .help-inline").text("").removeClass("errorMsg").removeClass("trueMsg");
            return;
        }
    }
    $("#password_confirm ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
}

function checkedEmail(){
    var email=$("#contact_email").val();
    var isEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if (email == ""){
        $("#contact_email ~ .help-inline").text("电子邮箱不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isEmail.test(email)){
        $("#contact_email ~ .help-inline").text("该邮箱地址不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.Application.itemIsExist(email, ITEM_TYPE_EMAIL).ajax({
        /*data: formData,
         processData: false,
         contentType: false,*/
        cache: false,
        async: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#contact_email  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#contact_email  ~ .help-inline').text("该邮箱已注册，请重新输入或登录").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#contact_email  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedTel(){
    var tel=$("#contact_tel").val();
    var isTel=/^\d{3,4}-\d{7,8}$/;
    if (tel == ""){
        $("#contact_tel ~ .help-inline").text("固定电话不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }

    if(!isTel.test(tel)){
        $("#contact_tel ~ .help-inline").text("电话号码不合法，请输入固定电话").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact_tel ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
    /*jsRoutes.controllers.Application.itemIsExist(tel, ITEM_TYPE_TEL).ajax({
        *//*data: formData,
         processData: false,
         contentType: false,*//*
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#contact_tel  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#contact_tel  ~ .help-inline').text("该号码已登记，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            alert(err.status+"出现异常错误");
        }
    });*/
}

function checkedName(){
    var salonName=$("#salonName").val();
    //var isName = /^\x{4e00}-\x{9fa5}\w+$/;
    if (salonName == ""){
        $("#salonName ~ .help-inline").text("店铺名不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.Application.itemIsExist(salonName, ITEM_TYPE_NAME).ajax({
        /*data: formData,
         processData: false,
         contentType: false,*/
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#salonName  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#salonName  ~ .help-inline').text("该店铺名已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#salonName  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedContact(){
    var salonName=$("#contact").val();
    if (salonName == ""){
        $("#contact ~ .help-inline").text("联系人不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedIndustry(){
    var Industry = $(':checkbox')
    var checked = false;
    for (var i = 0; i < Industry.length; i++){
        checked = (Industry[i].checked ||checked)
    }
    if (!checked) {
        $('.salonIndustry  ~ .help-inline').text('请选择店铺行业种类').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $('.salonIndustry  ~ .help-inline').text('').removeClass("errorMsg").addClass("trueMsg");
    }
}

function checksOfSalonRegister(){
    checkedAccountId();
    checkedPassword();
    checkedPasswordConfirm();
    checkedEmail();
    checkedTel();
    checkedName();
    checkedContact();
    checkedIndustry();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.salonRegister.submit();
}