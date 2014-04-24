/**
 * Created by YS-HAN on 14/04/23.
 */
var ITEM_TYPE_ID = "loginId"
var ITEM_TYPE_NAME = "name"
var ITEM_TYPE_EMAIL = "email"
var ITEM_TYPE_TEL = "tel"

$('#accountId').change(function(){
    checkedAccountId();
});
$('#password_main').change(function(){
    checkedPassword()
});
$('#password_confirm').change(function(){
    checkedPasswordConfirm()
});
$('#contact_email').change(function(){
    checkedEmail()
});
$('#contact_tel').change(function(){
    checkedTel()
});
$('#salonName').change(function(){
    checkedSalonName()
});
$('#salonNameAbbr').change(function(){
    checkedSalonNameAbbr()
});
$('#salonDescription').change(function(){
    checkedDescription()
});
$('#contact').change(function(){
    checkedContact()
});
$('.salonIndustry').change(function(){
    checkedIndustry()
});
$('#addressDetail').blur(function(){
    checkedAddressDetail()
});
$('#accessMethodDesc').change(function(){
    checkedAccessMethodDesc()
});
$('#openTime').blur(function(){
    checkedOpenTime()
});
$('#closeTime').change(function(){
    checkedCloseTime()
});
$('#establishDate').change(function(){
    checkedEstablishDate()
});
$('.week').change(function(){
    checkedRestDays()
});
$('#restDay2').change(function(){
    checkedRestDays()
});
$('#seatNums').change(function(){
    checkedSeatNums()
});
$('#picTitle').change(function(){
    checkedPicTitle()
});
$('#picContent').change(function(){
    checkedPicContent()
});
$('#picFoot').change(function(){
    checkedPicFoot()
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

function checkedSalonName(){
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

function checkedSalonNameAbbr(){
    var salonNameAbbr=$("#salonNameAbbr").val();
    //var isName = /^\x{4e00}-\x{9fa5}\w+$/;
    if (salonNameAbbr == ""){
        $("#salonNameAbbr ~ .help-inline").text("店铺名略称不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    /*jsRoutes.controllers.Application.itemIsExist(salonNameAbbr, ITEM_TYPE_NAME).ajax({
        *//*data: formData,
         processData: false,
         contentType: false,*//*
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
    });*/
}

function checkedDescription(){
    var value=$("#salonDescription").val();
    if (value == ""){
        $("#salonDescription ~ .help-inline").text("简介不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else if(value.length > 100){
        $("#salonDescription ~ .help-inline").text("最多可输入50个字符").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#salonDescription ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedAddressDetail(){
    var value=$("#addrDetail").val();
    if (value == ""){
        $("#addrDetail ~ .help-inline").text("店铺地址不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#addrDetail ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedContact(){
    var value=$("#contact").val();
    if (value == ""){
        $("#contact ~ .help-inline").text("联系人不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#contact ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedOpenTime(){
    var value=$("#openTime").val();
    if (value == ""){
        $("#openTime ~ .help-inline").text("营业开始时间不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#openTime ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedCloseTime(){
    var value=$("#closeTime").val();
    if (value == ""){
        $("#closeTime ~ .help-inline").text("营业结束时间不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#closeTime ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedEstablishDate(){
    var value=$("#establishDate").val();
    if (value == ""){
        $("#establishDate ~ .help-inline").text("开业日期不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#establishDate ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

function checkedAccessMethodDesc(){
    var value=$("#accessMethodDesc").val();
    if (value == ""){
        $("#accessMethodDesc ~ .help-inline").text("交通方法不能为空").removeClass("trueMsg").addClass("errorMsg");
        return;
    }else{
        $("#accessMethodDesc ~ .help-inline").text("").removeClass("errorMsg").addClass("trueMsg");
    }
}

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
        $("#contact_tel ~ .help-inline").text("席位数不能为空").removeClass("trueMsg").addClass("errorMsg");
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
    checkedDescription();
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
$('#couponName').blur(function(){
    checkedCouponName()
});
function checkedCouponName(){
    var value = $('#couponName').val();
    var isValid=/^[a-zA-Z][a-zA-Z0-9_]{2,15}$/;

    if (value == ""){
        $("#couponName").parent("dd").next().text('').removeClass("trueMsg").addClass("errorMsg");
        return;
    }
    if(!isValid.test(value)){
        $("#couponName").parent("dd").next().text('该优惠券名不合法，请重新输入').removeClass("trueMsg").addClass("errorMsg");
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

$('#startDate').blur(function(){
    var startDate = $(this).val();
    var endDate = $('#endDate').val();
    if(startDate == "") {
        $("#startDate").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        if(endDate != "" && startDate > endDate) {
            $("#startDate").parent("dd").next().text('@Messages("coupon.startDateltEndMsg")').removeClass("trueMsg").addClass("errorMsg");
            return;
        } else {
            $("#startDate").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }
});

$('#endDate').blur(function(){
    var endDate = $(this).val();
    var startDate = $('#startDate').val();
    if(endDate == "") {
        $("#endDate").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        if(startDate != "" && startDate > endDate) {
            $("#endDate").parent("dd").next().text('@Messages("coupon.endDateGtStartMsg")').removeClass("trueMsg").addClass("errorMsg");
            return;
        } else {
            $("#endDate").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }
});

$('#perferentialPrice').blur(function(){
    var perferentialPrice = $(this).val();
    if(perferentialPrice == "") {
        $("#perferentialPrice").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        if(isNaN(perferentialPrice)){
            $("#perferentialPrice").parent("dd").next().text('@Messages("coupon.perferentialPriceErr")').removeClass("trueMsg").addClass("errorMsg");
            return;
        }else{
            $("#perferentialPrice").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
        }
    }
});

$('#useConditions').blur(function(){
    var useConditions = $(this).val();
    if(useConditions == "") {
        $("#useConditions").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#useConditions").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
});

$('#presentTime').blur(function(){
    var presentTime = $(this).val();
    if(presentTime == "") {
        $("#presentTime").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#presentTime").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
});

$('#presentTime').blur(function(){
    var presentTime = $(this).val();
    if(presentTime == "") {
        $("#presentTime").parent("dd").next().text('@Messages("common.requiredMsg")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#presentTime").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
});

$('#description').blur(function(){
    var description = $(this).val();
    if(description.length < 10 || description.length > 100) {
        $("#description").parent("dd").next().text('@Messages("coupon.couponDiscriptionErr")').removeClass("trueMsg").addClass("errorMsg");
        return;
    } else {
        $("#description").parent("dd").next().text("").removeClass("errorMsg").addClass("trueMsg");
    }
});
