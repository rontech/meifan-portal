/**
 * Created by YS-HAN on 14/04/23.
 */
var ITEM_TYPE_ID = "loginId"
var ITEM_TYPE_NAME = "name"
var ITEM_TYPE_NAME_ABBR = "nameAbbr"
var ITEM_TYPE_EMAIL = "email"
var ITEM_TYPE_TEL = "tel"
var ITEM_TYPE_STYLE = "style"
var ITEM_TYPE_COUPON = "coupon"
var ITEM_TYPE_SERVICE = "service"
var ITEM_TYPE_MENU = "menu"

/**
 * listening userId and salonAccountId
 */
$('#accountId').change(function(){
    checkedAccountId();
});
/**
 * listening password
 */
$('#password_main').change(function(){
    checkedPassword()
});
$('#password_confirm').change(function(){
    checkedPasswordConfirm()
});
/**
 * listening emails in user,salon
 */
$('#contact_email').change(function(){
    checkedEmail()
});
/**
 * listening fixed-line telephone in salon
 */
$('#contact_tel').change(function(){
    checkedTel()
});
/**
 * listening salonName
 */
$('#salonName').change(function(){
    checkedSalonName()
});
/**
 * listening salonNameAbbr
 */
$('#salonNameAbbr').change(function(){
    checkedSalonNameAbbr()
});
/**
 * listening salonDescription
 */
$('#salonDescription').change(function(){
    checkedSalonDescription()
});
/**
 * listening contact in salon
 */
$('#contact').change(function(){
    checkedContact()
});
/**
 * listening checkBoxes of salonIndustry
 */
$('.salonIndustry').change(function(){
    checkedIndustry()
});
/**
 * listening addressDetail of salon
 */
$('#addressDetail').blur(function(){
    checkedAddressDetail()
});
/**
 * listening accessMethodDesc of salon
 */
$('#accessMethodDesc').change(function(){
    checkedAccessMethodDesc()
});
/**
 * listening openTime and closeTime of Salon
 */
$('#openTime').change(function(){
    checkedOpenTime()
});
$('#closeTime').change(function(){
    checkedCloseTime()
});
/**
 * listening establishDate of salon
 */
$('#establishDate').change(function(){
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
$('#restDay2').change(function(){
    checkedRestDays()
});
/**
 * listening seat's number of Salon
 */
$('#seatNums').change(function(){
    checkedSeatNums()
});
/**
 * listening the descriptions of picture in salon
 */
$('#picTitle').change(function(){
    checkedPicTitle()
});
$('#picContent').change(function(){
    checkedPicContent()
});
$('#picFoot').change(function(){
    checkedPicFoot()
});
/**
 * listening phone in user,salon contact
 */
$('#phone').change(function(){
    checkedPhone();
});
/**
 * listening nickName in user
 */
$('#nickName').change(function(){
    checkedNickName();
});

/**
 * check for userId and salonAccountId
 */
function checkedAccountId(){
    var salonId = $('#accountId').val();
    var isName=/^[a-zA-Z][a-zA-Z0-9_]{5,17}$/;
    if (salonId == ""){
        $('#accountId  ~ .help-inline').text("登录ID不能为空").removeClass("trueMsg").addClass("errorMsg");
        $("#accountId").parent("dd").next().text("登录ID不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isName.test(salonId)){
        $('#accountId  ~ .help-inline').text('该登录ID不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
        $("#accountId").parent("dd").next().text('该登录ID不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(salonId, ITEM_TYPE_ID).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#accountId  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
                $("#accountId").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#accountId  ~ .help-inline').text("该登录ID已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
                $("#accountId").parent("dd").next().text("该登录ID已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error:function(err){
            $('#accountId  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
            $("#accountId").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
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
        $('#password_main  ~ .help-inline').text('密码不能为空').removeClass("trueMsg").addClass("errorMsg");
        $("#password_main").parent("dd").next().text('密码不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isPassword.test(password)){
        $("#password_main ~ .help-inline").text("密码不合法").removeClass("trueMsg").addClass("errorMsg");
        $("#password_main").parent("dd").next().text("密码不合法").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    $("#password_main ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    $("#password_main").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
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
            $("#password_confirm").parent("dd").next().text('请再次输入密码').removeClass("trueMsg").addClass("errorMsg");
            return;
        }
        $("#password_confirm ~ .help-inline").text("两次输入的密码不一致，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        $("#password_confirm").parent("dd").next().text("两次输入的密码不一致，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        if (password == ""){
            $("#password_confirm ~ .help-inline").text("").removeClass("errorMsg").removeClass("trueMsg");
            $("#password_confirm").parent("dd").next().text("").removeClass("errorMsg").removeClass("trueMsg");
            return;
        }
    }
    $("#password_confirm ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    $("#password_confirm").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
}
/**
 * check for email
 */
function checkedEmail(){
    var email=$("#contact_email").val();
    var isEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if (email == ""){
        $("#contact_email ~ .help-inline").text("电子邮箱不能为空").removeClass("trueMsg").addClass("errorMsg");
        $("#contact_email").parent("dd").next().text("电子邮箱不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isEmail.test(email)){
        $("#contact_email ~ .help-inline").text("该邮箱地址不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        $("#contact_email").parent("dd").next().text("该邮箱地址不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(email, ITEM_TYPE_EMAIL).ajax({
        cache: false,
        async: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#contact_email  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
                $("#contact_email").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#contact_email  ~ .help-inline').text("该邮箱已注册，请重新输入或登录").removeClass("trueMsg").addClass("errorMsg");
                $("#contact_email").parent("dd").next().text("该邮箱已注册，请重新输入或登录").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            alert("123123"+err.status);
            $('#contact_email  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
            $("#contact_email").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

/**
 * check fixed-line telephone in salon
 */
function checkedTel(){
    var tel=$("#contact_tel").val();
    var isTel=/^\d{3,4}-\d{7,8}$/;
    if (tel == ""){
        $("#contact_tel ~ .help-inline").text("固定电话不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isTel.test(tel)){
        $("#contact_tel ~ .help-inline").text("电话号码不合法，请输入固定电话").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#contact_tel ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
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
        $("#salonName ~ .help-inline").text("店铺名不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 4||len  > 40){
        $("#salonName ~ .help-inline").text("店铺名长度不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(salonName, ITEM_TYPE_NAME).ajax({
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

/**
 * check for salonNameAbbr
 */
function checkedSalonNameAbbr(){
    var salonNameAbbr=$("#salonNameAbbr").val();
    var len = salonNameAbbr.replace(/[^\x00-\xff]/g, "**").length;
    if (salonNameAbbr == ""){
        $("#salonNameAbbr ~ .help-inline").text("店铺名略称不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 4||len  > 40){
        $("#salonNameAbbr ~ .help-inline").text("店铺名略称不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(salonNameAbbr, ITEM_TYPE_NAME_ABBR).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#salonNameAbbr  ~ .help-inline').text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#salonNameAbbr  ~ .help-inline').text("该店铺名略称已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#salonNameAbbr  ~ .help-inline').text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedNickName(){
    var nickName=$("#nickName").val();
    var len = nickName.replace(/[^\x00-\xff]/g, "**").length;
    if (nickName == ""){
        $("#nickName").parent("dd").next().text("昵称不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if (len < 1||len  > 10){
        $("#nickName").parent("dd").next().text("昵称不合法，请输入1~10位字符").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.noAuth.Users.checkIsExist(nickName, ITEM_TYPE_NAME).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $("#nickName").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#nickName").parent("dd").next().text("该昵称已被使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            alert("err" +err.status);
            $("#nickName").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

/**
 * check for salon's description
 */
function checkedSalonDescription(){
    var value=$("#salonDescription").val();
    if (value == ""){
        $("#salonDescription ~ .help-inline").text("简介不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#salonDescription ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for detail address of salon
 */
function checkedAddressDetail(){
    var value=$("#addrDetail").val();
    if (value == ""){
        $("#addrDetail ~ .help-inline").text("店铺地址不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#addrDetail ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for contact of salon
 */
function checkedContact(){
    var value=$("#contact").val();
    //TODO
    if (value == ""){
        $("#contact ~ .help-inline").text("联系人不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for openTime of salon
 */
function checkedOpenTime(){
    var value=$("#openTime").val();
    if (value == ""){
        $("#openTime ~ .help-inline").text("营业开始时间不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#openTime ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for closeTime of salon
 */
function checkedCloseTime(){
    var value=$("#closeTime").val();
    if (value == ""){
        $("#closeTime ~ .help-inline").text("营业结束时间不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#closeTime ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for establish date of salon
 */
function checkedEstablishDate(){
    var value=$("#establishDate").val();
    if (value == ""){
        $("#establishDate ~ .help-inline").text("开业日期不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#establishDate ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for access method of salon
 */
function checkedAccessMethodDesc(){
    var value=$("#accessMethodDesc").val();
    if (value == ""){
        $("#accessMethodDesc ~ .help-inline").text("交通方法不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#accessMethodDesc ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

/**
 * check for restDay of salon
 */
function checkedRestDays(){
    if ($("#restDays_restWay_Fixed").attr("checked")){
        var restDay = $('.week')
        var checked = false;
        for (var i = 0; i < restDay.length; i++){
            checked = (restDay[i].checked ||checked)
        }
        if (!checked) {
            $(".week ~ .help-inline").text("请选择固定休息日").removeClass("trueMsg").addClass("errorMsg");
         }else{
            $(".week ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }else if ($("#restDays_restWay_Indefinite").attr("checked")){
        var restDay = $('#restDay2').val();
        if (restDay == ""){
            $("#restDay2 ~ .help-inline").text("休息规则不能为空").removeClass("trueMsg").addClass("errorMsg");
        }else{
            $("#restDay2 ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }else {
        $("#restDays_restWay_Indefinite ~ .help-inline").text("请选择店铺休息方式").removeClass("trueMsg").addClass("errorMsg");
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

function checkedSeatNums(){
    var value=$("#seatNums").val();
    var isValid=/^[1-9]\d*$/;
    if (value == ""){
        $("#seatNums ~ .help-inline").text("席位数不能为空").removeClass("trueMsg").addClass("errorMsg");
    }
    if(!isValid.test(value)){
        $("#seatNums ~ .help-inline").text("输入不合法，请输入数字").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#seatNums ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicTitle(){
    var value=$("#picTitle").val();
    if (value == ""){
        $("#picTitle ~ .help-inline").text("沙龙简介标题不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picTitle ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicContent(){
    var value=$("#picContent").val();
    if (value == ""){
        $("#picContent ~ .help-inline").text("沙龙简介不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picContent ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}
function checkedPicFoot(){
    var value=$("#picFoot").val();
    if (value == ""){
        $("#picFoot ~ .help-inline").text("沙龙简介标注不能为空").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#picFoot ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedPhone(){
    var phone = $("#phone").val();
    var isPhone = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
    
    if(phone != "" && !isPhone.test(phone)){
        $("#phone").parent("dd").next().text("该手机号不合法，请重新输入").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
    	$("#phone").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
    }

function checksForUserRegister(){
    //当为修改注册信息页面时，用户ID：readonly；
    //当为注册页面时，用户ID为必填项；
    if (!$('#accountId').attr("readonly")){
        checkedAccountId();
        checkedPassword();
        checkedPasswordConfirm();
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
    if (!$('#accountId').attr("readonly")){
        checkedAccountId();
        checkedPassword();
        checkedPasswordConfirm();
        checkedSalonName();
        checkedEmail();
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
$('#couponName').change(function(){
    checkedCouponName()
});
function checkedCouponName(){
    var value = $('#couponName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#couponName").parent("dd").next().text('优惠券名不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4|| len > 40){
        $("#couponName").parent("dd").next().text('该优惠券名不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_COUPON).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){

                $("#couponName").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#couponName").parent("dd").next().text("该优惠券名已使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            alert("123123123123"+err.status);
            $("#couponName").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checkedStartAndEndDate(){
    var startDate = $('#startDate').val();
    var endDate = $('#endDate').val();

    if(endDate != "") {
        $("#endDate").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
    if(startDate == "") {
        $("#startDate").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    $("#startDate").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    if(endDate == "") {
        $("#endDate").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(startDate > endDate) {
        $("#endDate").parent("dd").next().text('@Messages("coupon.startDateltEndMsg")').removeClass("trueMsg").addClass("errorMsg");
    } else {
        $("#endDate").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#price').blur(function(){
    checkedPrice()
});

function checkedPrice(){
    var price = $('#price').val();
    if(price == "") {
        $("#price").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
    } else {
        if(isNaN(price)){
            $("#price").parent("dd").next().text('@Messages("coupon.perferentialPriceErr")').removeClass("trueMsg").addClass("errorMsg");
            return;
        }else{
            $("#price").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }
}

$('#useConditions').blur(function(){
    checkedUesConditions()
});
function checkedUesConditions(){
    var useConditions = $('#useConditions').val();
    if(useConditions == "") {
        $("#useConditions").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#useConditions").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#presentTime').blur(function(){
    checkedPresentTime()
});
function checkedPresentTime(){
    var presentTime = $('#presentTime').val();
    if(presentTime == "") {
        $("#presentTime").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#presentTime").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

$('#description').blur(function(){
    checkedItemDescription()
});

function checkedItemDescription(){
    var value = $('#description').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if(len < 10 || len > 100) {
        $("#description").parent("dd").next().text('@Messages("coupon.couponDiscriptionErr")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#description").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
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
        $('.serviceItem').parent("div").parent("dd").next().text('请选择服务').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $('.serviceItem').parent("div").parent("dd").next().text('').removeClass("errorMsg").addClass("trueMsg");
    }
}

function checksForCoupon(){
    if (!$('#couponName').attr("readonly")){
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

$('#menuName').change(function(){
    checkedMenuName();
});

function checkedMenuName(){
    var value = $('#menuName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#menuName").parent("dd").next().text('菜单名不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#menuName").parent("dd").next().text('该菜单名不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#menuName").parent("dd").next().text('').removeClass("errorMsg").addClass("trueMsg");
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_COUPON).ajax({
     async: false,
     cache: false,
     type: 'POST',
     success: function(data){
     if (data == "false"){
         $("#menuName").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
     }
     else{
         $("#menuName").parent("dd").next().text("该菜单名已使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
     }
     },
     error: function(err){
         $("#menuName").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
     }
     });
}

function checksForMenu(){
    if (!$('#menuName').attr("readonly")){
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
$('#serviceName').change(function(){
   checkedServiceName();
});

function checkedServiceName(){
    var value = $('#serviceName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#serviceName").parent("dd").next().text('服务名不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#serviceName").parent("dd").next().text('该服务名不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#serviceName").parent("dd").next().text('').removeClass("errorMsg").addClass("trueMsg");
    }
    jsRoutes.controllers.auth.Salons.itemIsExist(value, ITEM_TYPE_COUPON).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $("#serviceName").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $("#serviceName").parent("dd").next().text("该服务名已使用，请重新输入").removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $("#serviceName").parent("dd").next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

$('#duration').blur(function(){
    checkedDuration();
});

function checkedDuration(){
    var value=$("#duration").val();
    var isValid=/^[1-9]\d*$/;
    if (value == ""){
        $("#duration").parent("dd").next().text("服务耗时不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isValid.test(value)){
        $("#duration").parent("dd").next().text("输入不合法，请输入整数").removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#duration").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checksForService(){
    if (!$('#serviceName').attr("readonly")){
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

$('#StyleName').change(function(){
    checkStyleName()
});

function checkStyleName(){
    var value = $('#StyleName').val();
    var len = value.replace(/[^\x00-\xff]/g, "**").length;
    if (value == ""){
        $("#StyleName").parent("dd").next().text('发型名不能为空').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(len < 4 ||len > 40){
        $("#StyleName").parent("dd").next().text('该发型名不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
    }else{
        $("#StyleName").parent("dd").next().text('').removeClass("errorMsg").addClass("trueMsg");
    }
    jsRoutes.controllers.auth.Stylists.itemIsExist(value, ITEM_TYPE_STYLE).ajax({
        async: false,
        cache: false,
        type: 'POST',
        success: function(data){
            if (data == "false"){
                $('#StyleName').parent('dd').next().text('').removeClass("errorMsg").addClass("trueMsg");
            }
            else{
                $('#StyleName').parent('dd').next().text('该发型名已使用').removeClass("trueMsg").addClass("errorMsg");
            }
        },
        error: function(err){
            $('#StyleName').parent('dd').next().text("很抱歉！检测失败，请稍候重试！").removeClass("trueMsg").addClass("errorMsg");
        }
    });
}

function checksForStyle(){
    checkStyleName();
    var errInput = $('.errorMsg')
    if (errInput.length != 0 ){
        return false;
    }
    document.styleForm.submit();
}