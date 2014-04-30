/**
 * Created by YS-HAN on 14/04/23.
 */
var ITEM_TYPE_ID = "loginId"
var ITEM_TYPE_NAME = "name"
var ITEM_TYPE_NAME_ABBR = "nameAbbr"
var ITEM_TYPE_EMAIL = "email"
var ITEM_TYPE_PHONE = "phone"
var ITEM_TYPE_STYLE = "style"
var ITEM_TYPE_COUPON = "coupon"
var ITEM_TYPE_SERVICE = "service"
var ITEM_TYPE_MENU = "menu"

var MESSAGE_OK = ""
var MESSAGE_REQUIRED = "该项目不能为空"
var MESSAGE_FORMAT_ERR = "格式不正确，请重新输入"
var MESSAGE_NAME_USED = "该名称已被使用，请重新输入"
var MESSAGE_NICKNAME_USED = "该昵称已被使用，请重新输入"
var MESSAGE_ID_USED = "该ID已被注册，请重新输入"
var MESSAGE_CHECK_ERR = "很抱歉！检测失败，请稍候重试"


/**
 * listening userId and salonAccountId
 */
$('#accountId').focus(function(){
    $('#accountId  ~ .help-inline').text("6~18位字符，可使用字母、数字和下划线，注册成功后不可修改").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedAccountId();
});
/**
 * listening password
 */
$('#password_main').focus(function(){
    $('#password_main  ~ .help-inline').text("6~16位字符,可使用字母、数字或符号的组合，区分大小写").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPassword()
});
$('#password_confirm').focus(function(){
    $('#password_confirm  ~ .help-inline').text("请再次输入密码").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPasswordConfirm()
});
/**
 * listening emails in user
 */
$('#email').focus(function(){
    $('#email  ~ .help-inline').text("请输入常用邮箱，用于账户激活、找回密码").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedEmail()
});
/**
 * listening emails in salon
 */
$('#contact_email').focus(function(){
    $('#contact_email  ~ .help-inline').text("请输入常用邮箱，用于账户激活、找回密码").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedSalonEmail()
});
/**
 * listening fixed-line telephone in salon
 */
$('#contact_tel').focus(function(){
    $('#contact_tel  ~ .help-inline').text("请填写联系人常用的电话，以便顾客联系，如：“0512-67776777”").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedTel()
});
/**
 * listening salonName
 */
$('#salonName').focus(function(){
    $('#salonName  ~ .help-inline').text("请填写工商局注册的全称。4~40位字符，可由中英文、数字及“_”、“-”、（）组成").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedSalonName()
});
/**
 * listening salonNameAbbr
 */
//TODO
$('#salonNameAbbr').focus(function(){
    $('#salonNameAbbr  ~ .help-inline').text("请填写工商局注册的全称。4~40位字符，可由中英文、数字及“_”、“-”、（）组成").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedSalonNameAbbr()
});
/**
 * listening salonDescription
 */
//TODO
$('#salonDescription').focus(function(){
    $('#salonDescription  ~ .help-inline').text("100字以内，该字段对应店铺基本信息表格中信息").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedSalonDescription()
});
/**
 * listening contact in salon
 */
$('#contact').focus(function(){
    $('#contact  ~ .help-inline').text("2~20位字符，可使用中文或英文组合").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedContact()
});
/**
 * listening checkBoxes of salonIndustry
 */
$('.salonIndustry').change(function(){
    checkedIndustry()
});

$('#accept').change(function(){
    checkedAccept()
})
/**
 * listening homepage of salon
 */
$('#homepage').focus(function(){
    $('#homepage  ~ .help-inline').text("请填写店铺主页，如：http://www.sz-rontech.com").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedHomepage()
});
/**
 * listening addressDetail of salon
 */
$('#addrDetail').focus(function(){
    $('#addrDetail  ~ .help-inline').text("请填写店铺详细地址，如“竹园路209号5号楼1805室”").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedAddressDetail()
});
/**
 * listening accessMethodDesc of salon
 */
$('#accessMethodDesc').focus(function(){
    $('#accessMethodDesc  ~ .help-inline').text("100字以内，如“地铁一号线汾湖路站1号出口向西步行500米可达”").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedAccessMethodDesc()
});
/**
 * listening openTime and closeTime of Salon
 */
$('#openTime').focus(function(){
    $('#openTime  ~ .help-inline').text("请填写店铺营业的开始时间").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedOpenTime()
});
$('#closeTime').focus(function(){
    $('#closeTime  ~ .help-inline').text("请填写店铺营业的结束时间").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedCloseTime()
});
/**
 * listening establishDate of salon
 */
$('#establishDate').focus(function(){
    $('#establishDate  ~ .help-inline').text("请填写店铺成立的时间").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedEstablishDate()
});
/**
 * listening checkBoxes of fixed restDay
 */
$('.week').change(function(){
    checkedRestDays()
});
/**
 * listening indefinite restDay
 */
$('#restDay2').focus(function(){
    $('#restDay2  ~ .help-inline').text("请填写店铺休息规律").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedRestDays()
});
/**
 * listening seat's number of Salon
 */
$('#seatNums').focus(function(){
    $('#seatNums  ~ .help-inline').text("请填写店铺席位数").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedSeatNums()
});
/**
 * listening the descriptions of picture in salon
 */
$('#picTitle').focus(function(){
    $('#picTitle  ~ .help-inline').text("100字以内，该描述将用于沙龙信息描述的标题显示").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPicTitle()
});
$('#picContent').focus(function(){
    $('#picContent  ~ .help-inline').text("100字以内，该描述将用于沙龙信息描述的主要内容显示").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPicContent()
});
$('#picFoot').focus(function(){
    $('#picFoot  ~ .help-inline').text("100字以内，该描述将用于沙龙信息描述的标注显示").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPicFoot()
});
/**
 * listening phone in user,salon contact
 */
$('#phone').focus(function(){
    $('#phone  ~ .help-inline').text('请输入常用手机号，服务预约时使用').removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedPhone();
});
/**
 * listening nickName in user
 */
$('#nickName').focus(function(){
    $('#nickName  ~ .help-inline').text('请输入昵称，1~10位字符').removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedNickName();
});

/**
 * check for userId and salonAccountId
 */
function checkedAccountId(){
    var salonId = $('#accountId').val();
    var isName=/^[a-zA-Z][a-zA-Z0-9_]{5,17}$/;
    if (salonId == ""){
        $('#accountId  ~ .help-inline').text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isName.test(salonId)){
        $('#accountId  ~ .help-inline').text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(salonId, ITEM_TYPE_ID).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#accountId  ~ .help-inline').text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#accountId  ~ .help-inline').text(MESSAGE_ID_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error:function(err){
            $('#accountId  ~ .help-inline').text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}
/**
 * check for password
 */
function checkedPassword(){
    var password=$('#password_main').val();
    var isPassword=/^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]{6,16}$/;
    if (password == ""){
        $('#password_main  ~ .help-inline').text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isPassword.test(password)){
        $("#password_main ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    $("#password_main ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
}

/**
 * check for password confirm
 */
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
            $("#password_confirm ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
            return;
        }
    }
    $("#password_confirm ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
}
/**
 * check for email
 */
function checkedEmail(){
    var email=$("#email").val();
    var isEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if (email == ""){
        $("#email ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isEmail.test(email)){
        $("#email ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(email, ITEM_TYPE_EMAIL).ajax({
        cache: false,
        async: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#email  ~ .help-inline').text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#email  ~ .help-inline').text("该邮箱已注册，请重新输入或登录").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#email  ~ .help-inline').text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedSalonEmail(){
    var email=$("#contact_email").val();
    var isEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if (email == ""){
        $("#contact_email ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isEmail.test(email)){
        $("#contact_email ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#contact_email ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
/**
 * check fixed-line telephone in salon
 */
function checkedTel(){
    var tel=$("#contact_tel").val();
    var isTel=/^\d{3,4}-\d{7,8}$/;
    if (tel == ""){
        $("#contact_tel ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isTel.test(tel)){
        $("#contact_tel ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#contact_tel ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedHomepage(){
    var homepage = $("#homepage").val();
    //var isUrl = /^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$/;
    if(homepage == ""){
        $("#homepage  ~ .help-inline").text(MESSAGE_OK).removeClass("trueMsg").removeClass("errorMsg");
        return;
    }
    //if(!isUrl.test(homepage)){
    if(false){
        $("#homepage  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#homepage  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
/**
 * check for salon Name
 */
function checkedSalonName(){
    //TODO
    var salonName=$("#salonName").val();
    var len = salonName.replace(/[^\x00-\xff]/g, "**").length;
    if (salonName == ""){
        $("#salonName ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 4||len  > 40){
        $("#salonName ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(salonName, ITEM_TYPE_NAME).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#salonName  ~ .help-inline').text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#salonName  ~ .help-inline').text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#salonName  ~ .help-inline').text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

/**
 * check for salonNameAbbr
 */
function checkedSalonNameAbbr(){
    var salonNameAbbr=$("#salonNameAbbr").val();
    var len = salonNameAbbr.replace(/[^\x00-\xff]/g, "**").length;
    if (salonNameAbbr == ""){
        $("#salonNameAbbr ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 4||len  > 40){
        $("#salonNameAbbr ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(salonNameAbbr, ITEM_TYPE_NAME_ABBR).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#salonNameAbbr  ~ .help-inline').text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#salonNameAbbr  ~ .help-inline').text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#salonNameAbbr  ~ .help-inline').text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedNickName(){
    var nickName=$("#nickName").val();
    var len = nickName.replace(/[^\x00-\xff]/g, "**").length;
    if (nickName == ""){
        $("#nickName  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 1||len  > 10){
        $("#nickName  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(nickName, ITEM_TYPE_NAME).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $("#nickName  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#nickName  ~ .help-inline").text(MESSAGE_NICKNAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $("#nickName  ~ .help-inline").text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

/**
 * check for salon's description
 */
function checkedSalonDescription(){
    var value=$("#salonDescription").val();
    if (value == ""){
        $("#salonDescription ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#salonDescription ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for detail address of salon
 */
function checkedAddressDetail(){
    var value=$("#addrDetail").val();
    if (value == ""){
        $("#addrDetail ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#addrDetail ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for contact of salon
 */
function checkedContact(){
    var value=$("#contact").val();
    //TODO
    if (value == ""){
        $("#contact ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for openTime of salon
 */
function checkedOpenTime(){
    var value=$("#openTime").val();
    if (value == ""){
        $("#openTime ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#openTime ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for closeTime of salon
 */
function checkedCloseTime(){
    var value=$("#closeTime").val();
    if (value == ""){
        $("#closeTime ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#closeTime ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for establish date of salon
 */
function checkedEstablishDate(){
    var value=$("#establishDate").val();
    if (value == ""){
        $("#establishDate ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#establishDate ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for access method of salon
 */
function checkedAccessMethodDesc(){
    var value=$("#accessMethodDesc").val();
    if (value == ""){
        $("#accessMethodDesc ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#accessMethodDesc ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for restDay of salon
 */
function checkedRestDays(){
    if ($("#restDays_restWay_Fixed").attr("checked")){
        $("#restDays_restWay ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
        $("#restDay2 ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
        var restDay = $('.week')
        var checked = false;
        for (var i = 0; i < restDay.length; i++){
            checked = (restDay[i].checked ||checked)
        }
        if (!checked) {
            $(".week ~ .help-inline").text("请选择固定休息日").removeClass("trueMsg").addClass("errorMsg");
         }else{
            $(".week ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
        }
    }else if ($("#restDays_restWay_Indefinite").attr("checked")){
        $("#restDays_restWay ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
        $(".week ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
        var restDay = $('#restDay2').val();
        if (restDay == ""){
            $("#restDay2 ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        }else{
            $("#restDay2 ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");

        }
    }else {
        $("#restDays_restWay ~ .help-inline").text("请选择店铺休息方式").removeClass("trueMsg").addClass("errorMsg");
    }
}

/**
 * check for checkBoxes of salonIndustry
 */
function checkedIndustry(){
    var Industry = $('.salonIndustry')
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
function checkedAccept(){
    if ($('#accept').attr("checked")){
        $('#accept ~ .help-inline').text("").removeClass("errorMsg");
    }else{
        $('#accept ~ .help-inline').text('您需要同意《美范网用户协议》').addClass("errorMsg");
    }
}


function checkedSeatNums(){
    var value=$("#seatNums").val();
    var isValid=/^[1-9]\d*$/;
    if (value == ""){
        $("#seatNums ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isValid.test(value)){
        $("#seatNums ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#seatNums ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicTitle(){
    var value=$("#picTitle").val();
    if (value == ""){
        $("#picTitle ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picTitle ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicContent(){
    var value=$("#picContent").val();
    if (value == ""){
        $("#picContent ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picContent ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicFoot(){
    var value=$("#picFoot").val();
    if (value == ""){
        $("#picFoot ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picFoot ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedPhone(){
    var phone = $("#phone").val();
    var isPhone = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;

    if(phone == ""){
        $("#phone  ~ .help-inline").text(MESSAGE_OK).removeClass("trueMsg").removeClass("errorMsg");
        return;
    }
    if(!isPhone.test(phone)){
        $("#phone  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
    	$("#phone  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
    }

function checksForUserRegister(){
    //当为修改注册信息页面时，用户ID：readonly；
    //当为注册页面时，用户ID为必填项；
    var userId = document.getElementsByName("userId");
    var isCreate = userId[0].getAttribute("readonly");
    if (!isCreate){
        checkedAccountId();
        checkedPassword();
        checkedPasswordConfirm();
        checkedAccept();
    }
    checkedEmail();
    checkedNickName();
    checkedPhone();
    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.userForm.submit();
}

function checksForSalonRegister(){
    //当为修改注册信息页面时，店铺ID：readonly；
    //当为注册页面时，店铺ID为必填项；
     var accountId = document.getElementsByName("salonAccount.accountId");
     var isCreate = accountId[0].getAttribute("readonly");
     if (!isCreate){
        checkedAccountId();
        checkedPassword();
        checkedPasswordConfirm();
        checkedSalonName();
        checkedSalonEmail();
        checkedAccept();
    }
    checkedTel();
    checkedContact();
    checkedIndustry();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.salonRegister.submit();
}

function checksForSalonBasic(){
    checkedSalonNameAbbr();
    checkedSalonDescription();
    checkedAddressDetail();
    checkedAccessMethodDesc();
    checkedOpenTime();
    checkedCloseTime();
    checkedEstablishDate();
    checkedRestDays();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.salonBasic.submit();
}

function checksForSalonDetail(){
    checkedSeatNums();
    checkedPicTitle();
    checkedPicContent();
    checkedPicFoot();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.salonDetail.submit();
}


/**
 * checks for coupon
 */
$('#couponName').focus(function(){
    $("#couponName  ~ .help-inline").text("请输入4~40个字符（一个汉字为2个字符）").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedCouponName()
});
function checkedCouponName(){
    var value = $('#couponName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#couponName  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4|| len > 40){
        $("#couponName  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_COUPON).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){

                $("#couponName  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#couponName  ~ .help-inline").text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $("#couponName  ~ .help-inline").text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

$('#startDate').focus(function(){
    $('#startDate  ~ .help-inline').text("请输入优惠券的开始时间").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedStartAndEndDate();
});

$('#endDate').focus(function(){
    $('#endDate  ~ .help-inline').text("请输入优惠券的结束时间").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedStartAndEndDate();
});

function checkedStartAndEndDate(){
    var startDate = $('#startDate').val();
    var endDate = $('#endDate').val();

    if(endDate != "") {
        $("#endDate  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
    if(startDate == "") {
        $("#startDate  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        $('#endDate  ~ .help-inline').text(MESSAGE_OK).removeClass("errorMsg").removeClass("trueMsg");
        return;
    }
    $("#startDate  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    if(endDate == "") {
        $("#endDate  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(startDate > endDate) {
        $("#endDate  ~ .help-inline").text('截至日期不能在开始日期之前').removeClass("trueMsg").addClass("errorMsg");
    } else {
        $("#endDate  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#price').focus(function(){
    $('#price  ~ .help-inline').text("请输入数字，可以为小数").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedPrice()
});

function checkedPrice(){
    var price = $('#price').val();
    if(price == "") {
        $("#price  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
    } else {
        if(isNaN(price)){
            $("#price  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
            return;
        }else{
            $("#price  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
        }
    }
}

$('#useConditions').focus(function(){
    $('#useConditions  ~ .help-inline').text("请输入该优惠劵使用的条件，如“不能和其他优惠劵一起使用”").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedUesConditions()
});
function checkedUesConditions(){
    var useConditions = $('#useConditions').val();
    if(useConditions == "") {
        $("#useConditions  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#useConditions  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#presentTime').focus(function(){
    $('#presentTime  ~ .help-inline').text("请输入使用该优惠劵出示的时间，如“消费前出示”").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedPresentTime()
});
function checkedPresentTime(){
    var presentTime = $('#presentTime').val();
    if(presentTime == "") {
        $("#presentTime  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#presentTime  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#description').focus(function(){
    $('#description  ~ .help-inline').text("请输入描述内容，10~50个字符（一个汉字为2个字符）").removeClass("errorMsg").removeClass("trueMsg");
}).blur(function(){
    checkedItemDescription()
});

function checkedItemDescription(){
    var value = $('#description').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;

    if (value == ""){
        $("#description  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 10 ) {
        $("#description  ~ .help-inline").text("描述内容不足").removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#description  ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

$('.serviceItem').change(function(){
    checkedServiceItem()
});
function checkedServiceItem(){
    var services = $('.serviceItem')
    var checked = false;
    for (var i = 0; i < services.length; i++){
        checked = (services[i].checked ||checked)
    }
    if (!checked) {
        $('.serviceItem').parent("div").next().text('请选择服务').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $('.serviceItem').parent("div").next().text('').removeClass("errorMsg").addClass("trueMsg");
    }
}

function checksForCoupon(){
        var couponName = document.getElementsByName("couponName");
        var isCreate = couponName[0].getAttribute("readonly");
        if (!isCreate){
        checkedCouponName();
    }
    checkedServiceItem();
    checkedPrice();
    checkedStartAndEndDate();
    checkedUesConditions();
    checkedPresentTime();
    checkedItemDescription();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.couponForm.submit();
}

/**
 *checks for menus
 */

$('#menuName').focus(function(){
    $("#menuName  ~ .help-inline").text("请输入4~40个字符（一个汉字为2个字符）").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedMenuName();
});

function checkedMenuName(){
    var value = $('#menuName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#menuName  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#menuName  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#menuName  ~ .help-inline").text('').removeClass("errorMsg").addClass("trueMsg");
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_MENU).ajax({
     async: false,
     cache: false,
     type: 'POST',
     success: function(data){
     if (data == "false"){
         $("#menuName  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
     }
     else{
         $("#menuName  ~ .help-inline").text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
     }
     },
     error: function(err){
         $("#menuName  ~ .help-inline").text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
     }
     });
}

function checksForMenu(){
    var menuName = document.getElementsByName("menuName");
    var isCreate = menuName[0].getAttribute("readonly");
    if (!isCreate){
        checkedMenuName();
    }
    checkedServiceItem();
    checkedItemDescription();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.menuForm.submit();
}

/**
 *checks for service
 */
$('#serviceName').focus(function(){
    $("#serviceName  ~ .help-inline").text("请输入4~40个字符（一个汉字为2个字符）").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
   checkedServiceName();
});

function checkedServiceName(){
    var value = $('#serviceName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#serviceName  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#serviceName  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#serviceName  ~ .help-inline").text('').removeClass("errorMsg").addClass("trueMsg");
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_SERVICE).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $("#serviceName  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#serviceName  ~ .help-inline").text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $("#serviceName  ~ .help-inline").text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

$('#duration').focus(function(){
    $("#duration  ~ .help-inline").text("请输入整数（单位：分）").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkedDuration();
});

function checkedDuration(){
    var value=$("#duration").val();
    var isValid=/^[1-9]\d*$/;
    if (value == ""){
        $("#duration  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isValid.test(value)){
        $("#duration  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#duration  ~ .help-inline").text(MESSAGE_OK).removeClass("errorMsg").addClass("trueMsg");
    }
}

function checksForService(){
    var name = document.getElementsByName("serviceName");
    var isCreate = name[0].getAttribute("readonly");
    if (!isCreate){
        checkedServiceName();
    }
    checkedPrice();
    checkedDuration();

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.serviceForm.submit();
}


/***
 *
 */

$('#styleName').focus(function(){
    $("#styleName  ~ .help-inline").text("请输入4~40个字符（一个汉字为2个字符）").removeClass("trueMsg").removeClass("errorMsg");
}).blur(function(){
    checkStyleName()
});

function checkStyleName(){
    var value = $('#styleName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#styleName  ~ .help-inline").text(MESSAGE_REQUIRED).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#styleName  ~ .help-inline").text(MESSAGE_FORMAT_ERR).removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.auth.Stylists.itemIsExist(value, ITEM_TYPE_STYLE).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#styleName  ~ .help-inline').text('').removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#styleName  ~ .help-inline').text(MESSAGE_NAME_USED).removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#styleName  ~ .help-inline').text(MESSAGE_CHECK_ERR).removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checksForStyle(){
    var styleName = document.getElementsByName("styleName");
    var isCreate = styleName[0].getAttribute("readonly");
    if (!isCreate){
        checkStyleName();
    }

    $('.picture_error_msg').remove();

    var $obj=$('.imageoffileobjid');

    if($obj.length == 1){
        $('.demo').append('<div class="picture_error_msg"><span class="errorMsg">至少选择一张图片</span><div>');
        return;
    }

    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.styleForm.submit();
}
