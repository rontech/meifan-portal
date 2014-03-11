// TODO
// 
$(function(){
		$("#emailLogin").click(function(){
			$("#usernameinf").text("邮箱：");
			$("#name").attr("name","email");
			});
		$("#nameLogin").click(function(){
			$("#usernameinf").text("用户名：");
			$("#name").attr("name","name");
			});
		$("#provice").click(function(){
			var provices=new Array("吉林省","辽宁省");
			});
		$("#name").blur(function(){
			var name=$(this).val();
			var isname=/^[a-zA-Z][a-zA-Z0-9_]{4,15}$/;

			if(!isname.test(name)){
			$("#nameMsg").text("用户名无效").removeClass("trueMsg").addClass("errorMsg");
			return;
			}

			$.get(
				"/checkNameisexit/"+name,
				{name:"zhangsan"},
				function(data){
				$("#nameMsg").text(data).removeClass("errorMsg").addClass("trueMsg");
				});

			});
		$("#password-main").blur(function(){
				var password=$(this).val();
				var ispassword=/^[a-zA-Z]\w{5,17}$/;
				if(!ispassword.test(password)){
				$("#passwordMainMsg").text("密码无效").removeClass("trueMsg").addClass("errorMsg");
				return;
				}
				$("#passwordMainMsg").text("可以使用").removeClass("errorMsg").addClass("trueMsg");
				});

		$("#password-confirm").blur(function(){
				var password=$(this).val();
				var ispassword=/^[a-zA-Z]\w{5,17}$/;
				if(!ispassword.test(password)){
				$("#passwordConfirmMsg").text("密码无效").removeClass("trueMsg").addClass("errorMsg");
				return;
				}
				var firstPassword=$("#password-main").val();
				if(password!=firstPassword){
				$("#passwordConfirmMsg").text("两次密码输入需一致").removeClass("trueMsg").addClass("errorMsg");
				return;
				}
				$("#passwordConfirmMsg").text("输入正确").removeClass("errorMsg").addClass("trueMsg");
				});
		$("#email").blur(function(){
				var email=$(this).val();
				var isemail=/^\w+([-+.]\w+)*@@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
				if(!isemail.test(email)){
				$("#emailMsg").text("邮箱无效").removeClass("trueMsg").addClass("errorMsg");
				return;
				}
				$("#emailMsg").text("可以使用").removeClass("errorMsg").addClass("trueMsg");
				});
		$("#phone").blur(function(){
				var phone=$(this).val();
				var isphone=/^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
				if(!isphone.test(phone)){
				$("#phoneMsg").text("号码无效").removeClass("trueMsg").addClass("errorMsg");
				return;
				}
				$("#phoneMsg").text("号码有效").removeClass("errorMsg").addClass("trueMsg");
				});
		//根据状态设置行背景色
		$().ready(function(){
				var $obj=$('#recordmain #service-status');
				$obj.each(function(i){
					var status=$(this).text().trim();
					if(status=="预约中"){
					$(this).parent().addClass("row1");
					}else if(status=="已过期"){
					$(this).parent().addClass('row2');
					}else if(status=="已消费"){
					$(this).parent().addClass('row3');
					}else if(status=="已取消"){
					$(this).parent().addClass('row4');
					}
					});
				});


})
//页面跳转
function toPage(page){
	document.getElementById("page").value=page;
	var pages=document.getElementById("page").value;
	var i=parseInt(page);
	var current="currentpage"+pages;
	window.location="/recordmain/"+i+"#";
}
//下一页
function toNextPage(){
	var totalPages=document.getElementById("totalPage").value;
	var currentPage=document.getElementById("page").value;
	if(totalPages==currentPage){
		return;
	}
	var i=parseInt(currentPage)+1;
	toPage(i);
}
//上一页
function toUpPage(){
	var totalPages=document.getElementById("totalPage").value;
	var currentPage=document.getElementById("page").value;
	if(1==currentPage){
		return;
	}
	var i=parseInt(currentPage)-1;
	toPage(i);
}


