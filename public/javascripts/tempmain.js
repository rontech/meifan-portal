// TODO
// 
$(function () {
  $("#emailLogin").click(function () {
    $("#usernameinf").text("邮箱：");
    $("#name").attr("name", "email");
  });
  $("#nameLogin").click(function () {
    $("#usernameinf").text("用户名：");
    $("#name").attr("name", "name");
  });
  $("#provice").click(function () {
    var provices = new Array("吉林省", "辽宁省");
  });
  $("#name").blur(function () {
    var name = $(this).val();
    var isname = /^[a-zA-Z][a-zA-Z0-9_]{4,15}$/;

    if (!isname.test(name)) {
      $("#nameMsg").text("用户名无效").removeClass("trueMsg").addClass("errorMsg");
      return;
    }

    $.get(
      "/checkNameisexit/" + name,
      {name: "zhangsan"},
      function (data) {
        $("#nameMsg").text(data).removeClass("errorMsg").addClass("trueMsg");
      });

  });
  $("#password-main").blur(function () {
    var password = $(this).val();
    var ispassword = /^[a-zA-Z]\w{5,17}$/;
    if (!ispassword.test(password)) {
      $("#passwordMainMsg").text("密码无效").removeClass("trueMsg").addClass("errorMsg");
      return;
    }
    $("#passwordMainMsg").text("可以使用").removeClass("errorMsg").addClass("trueMsg");
  });

  $("#password-confirm").blur(function () {
    var password = $(this).val();
    var ispassword = /^[a-zA-Z]\w{5,17}$/;
    if (!ispassword.test(password)) {
      $("#passwordConfirmMsg").text("密码无效").removeClass("trueMsg").addClass("errorMsg");
      return;
    }
    var firstPassword = $("#password-main").val();
    if (password != firstPassword) {
      $("#passwordConfirmMsg").text("两次密码输入需一致").removeClass("trueMsg").addClass("errorMsg");
      return;
    }
    $("#passwordConfirmMsg").text("输入正确").removeClass("errorMsg").addClass("trueMsg");
  });
  $("#email").blur(function () {
    var email = $(this).val();
    var isemail = /^\w+([-+.]\w+)*@@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    if (!isemail.test(email)) {
      $("#emailMsg").text("邮箱无效").removeClass("trueMsg").addClass("errorMsg");
      return;
    }
    $("#emailMsg").text("可以使用").removeClass("errorMsg").addClass("trueMsg");
  });
  $("#phone").blur(function () {
    var phone = $(this).val();
    var isphone = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
    if (!isphone.test(phone)) {
      $("#phoneMsg").text("号码无效").removeClass("trueMsg").addClass("errorMsg");
      return;
    }
    $("#phoneMsg").text("号码有效").removeClass("errorMsg").addClass("trueMsg");
  });
  //根据状态设置行背景色
  $().ready(function () {
    var $obj = $('#recordmain #service-status');
    $obj.each(function (i) {
      var status = $(this).text().trim();
      if (status == "预约中") {
        $(this).parent().addClass("row1");
      } else if (status == "已过期") {
        $(this).parent().addClass('row2');
      } else if (status == "已消费") {
        $(this).parent().addClass('row3');
      } else if (status == "已取消") {
        $(this).parent().addClass('row4');
      }
    });
  });


  $('.deleteBlog').click(function (e) {
    e.preventDefault();
    blogId = $(this).data('blog');
    if (!confirm("文章删除后无法恢复，请确认是否删除此篇文章？")) {
      return;
    } else {
      location.href = "/myPage/deleteBlog/" + blogId;
    }
  });

  $('.deleteComment').click(function (e) {
    e.preventDefault();
    commentId = $(this).data('comment');
    blogId = $(this).data('blog');
    if (!confirm("您确认要删除此篇评论？")) {
      return;
    } else {
      location.href = "/myPage/blog/delete/" + commentId + "/" + blogId;
    }
  });


})
//页面跳转
function toPage(page) {
  document.getElementById("page").value = page;
  var pages = document.getElementById("page").value;
  var i = parseInt(page);
  var current = "currentpage" + pages;
  window.location = "/recordmain/" + i + "#";
}
//下一页
function toNextPage() {
  var totalPages = document.getElementById("totalPage").value;
  var currentPage = document.getElementById("page").value;
  if (totalPages == currentPage) {
    return;
  }
  var i = parseInt(currentPage) + 1;
  toPage(i);
}
//上一页
function toUpPage() {
  var totalPages = document.getElementById("totalPage").value;
  var currentPage = document.getElementById("page").value;
  if (1 == currentPage) {
    return;
  }
  var i = parseInt(currentPage) - 1;
  toPage(i);
}

//删除提示
function del(url, msg) {
  if (!confirm(msg)) {
    return;
  } else {
    location.href = url;
  }
}

/**
 * 把当前页面的url截取相对路径，
 * 如果是首页就返回city，只是做跳转使用，
 * city本身没有意思，如果置空，会出现错误
 * 如果是其它页面就返回截取后的地址
 * @returns {*}
 * @constructor
 */
function GetRelativePath(){
  var strUrl = document.location.toString();
  var arrObj = strUrl.split("//");
  var start = arrObj[1].indexOf("/");
  if(arrObj[1].substring(start) == '/'){
    return '/city';
  }else{
    return arrObj[1].substring(start);//stop省略，截取从start开始到结尾的所有字符
  }
}

/**
 * 切换当前城市，向服务器请求到所有城市页面中
 * 并把当前所在页面的url，截取相对路径传送到服务器段保存到session中
 */
function switchCity() {
  location.href = "/getAllCitys"+GetRelativePath();
}

/**
 * 调用该js的页面加载后执行，
 * 先判断cookie是否有存放设置的城市，
 * 没有就通过IP获取城市设置到对应的位置，
 * 并且把城市保存到cookie中
 * expires: 7 表示可保存7天
 * path: '/' cookie位置为网站的根目录，所有页面都可访问
 * 再把城市已ajax方式发送到服务器保存起来
 */
$(document).ready(function () {
  if ($.cookie('myCity') == undefined || $.cookie('myCity') == null) {
    $.getScript('http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js', function () {
      $('.city_selected').text(remote_ip_info.city);
      $.cookie('myCity', remote_ip_info.city, { expires: 7, path: '/' });
    });
    $.ajax({
      type: "GET",
      url: "/setCityInSession/"+remote_ip_info.city,
      dataType: "text",
      success: function(data){
        return false;
      }
    });

  } else {
    var city = $.cookie('myCity');
    $('.city_selected').text(city);
  }
});

/**
 * 在所有城市目录中选择城市后，
 * 先把城市保存在cookie中，
 * 再把城市设置到页面指定位置，
 * 最后把城市传送到服务器，保存在session中
 * @param obj 选择城市的节点
 */
function selectCty(obj) {
  var city = obj.innerHTML.trim();
  $.cookie('myCity', city, { expires: 7, path: '/' });
  $('.city_selected').text(city);
  location.href = '/getOneCity/'+city;
}