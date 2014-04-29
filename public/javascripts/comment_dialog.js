//创建评论对话框
function create_comment_dialog(commentObjId, commentObjType) {
    var complex = "0";   //记录complex项的评 结果
    var atmosphere = "0";   //记录atmosphere项的评 结果
    var service = "0";   //记录service项的评 结果
    var skill = "0";   //记录skill项的评 结果
    var price = "0";   //记录price项的评 结果
    
    //创建遮罩层
    createShadeDiv();
    window.onresize = function () { createShadeDiv(); } //改变窗体重新调整位置
    window.onscroll = function () { createShadeDiv(); } //滚动窗体重新调整位置
    //创建对话框
    var mainDiv = jQuery("<div id='messageDialog' style='z-index:999;background:#fff;position:absolute;width:370px;height:400px;left:300px;top:300px;border:9px #646464 solid;-moz-border-radius: 7px;-khtml-border-radius: 7px;-webkit-border-radius: 7px;border-radius: 7px;'></div>");
    var headDiv = jQuery("<div id='title_bar' style='cursor:move;position:absolute;width:100%;height:35px;background:url(../images/comment_dialog/bar.jpg);'></div>");
    var title = jQuery("<div style='margin-top:8px;margin-left:30px;font-weight:bold;font-size:large'>评价</div>");
    var contentDiv = jQuery("<div style='width:100%;position:absolute;top:35px;padding:20px;font-weight:bold';color:#494949;></div>");
    var form = jQuery("<form action='' id = 'myForm' method = 'get'></form>");
    var table = jQuery("<table></table>");
    var firstLine = jQuery("<tr><td align='right'>综合</td><td><div id='complex2' ><ul class='rating nostar'><li class='one'><a href='#' title='1 '>1</a></li><li class='two'><a href='#' title='2 '>2</a></li><li class='three'><a href='#' title='3 '>3</a></li><li class='four'><a href='#' title='4 '>4</a></li><li class='five'><a href='#' title='5 '>5</a></li></ul></div></td></tr>");
    var secondLine = jQuery("<tr><td align='right'>氛围</td><td><div id='atmosphere2'><ul class='rating nostar'><li class='one'><a href='#' title='1 '>1</a></li><li class='two'><a href='#' title='2 '>2</a></li><li class='three'><a href='#' title='3 '>3</a></li><li class='four'><a href='#' title='4 '>4</a></li><li class='five'><a href='#' title='5 '>5</a></li></ul></div></td></tr>");
    var thirdLine = jQuery("<tr><td align='right'>服务</td><td><div id='service2' ><ul class='rating nostar'><li class='one'><a href='#' title='1 '>1</a></li><li class='two'><a href='#' title='2 '>2</a></li><li class='three'><a href='#' title='3 '>3</a></li><li class='four'><a href='#' title='4 '>4</a></li><li class='five'><a href='#' title='5 '>5</a></li></ul></div></td></tr>");
    var skillLine = jQuery("<tr><td align='right'>技术</td><td><div id='skill2' ><ul class='rating nostar'><li class='one'><a href='#' title='1 '>1</a></li><li class='two'><a href='#' title='2 '>2</a></li><li class='three'><a href='#' title='3 '>3</a></li><li class='four'><a href='#' title='4 '>4</a></li><li class='five'><a href='#' title='5 '>5</a></li></ul></div></td></tr>");
    var priceLine = jQuery("<tr><td align='right'>价格</td><td><div id='price2' ><ul class='rating nostar'><li class='one'><a href='#' title='1 '>1</a></li><li class='two'><a href='#' title='2 '>2</a></li><li class='three'><a href='#' title='3 '>3</a></li><li class='four'><a href='#' title='4 '>4</a></li><li class='five'><a href='#' title='5 '>5</a></li></ul></div></td></tr>");
    var fourthLine = jQuery("<tr><td colspan='2' align='center'><textarea id='content2'  style='width:320px;height:60px'></textarea></td></tr>");
   
//    var f1 = jQuery("<input type='hidden'  id='complex1' name='complex'>");
    var f1 = jQuery("<input type='hidden'  id='complex1' name='complex'>");
    var f2 = jQuery("<input type='hidden'  id='atmosphere1' name='atmosphere'>");
    var f3 = jQuery("<input type='hidden'  id='service1' name='service'>");
    var f4 = jQuery("<input type='hidden'  id='skill1' name='skill'>");
    var f5 = jQuery("<input type='hidden'  id='price1' name='price'>");
    var f6 = jQuery("<input type='hidden'  id='content1' name='content'>");
    var fifthLine = jQuery("<input type='button' value='提交' id='send_message'>");
    
//    var fifthLine = jQuery("<tr><td colspan='2' align='center'><p id='send_message' >提交</p></td></tr>");
    var closeImg = jQuery("<input id='closeImg' type='button' value='关闭' style='position:absolute;right:20px;top:10px;cursor:pointer'/>");

    title.appendTo(headDiv);
    headDiv.appendTo(mainDiv);

    firstLine.appendTo(table);
    secondLine.appendTo(table);
    thirdLine.appendTo(table);
    skillLine.appendTo(table);
    priceLine.appendTo(table);
    fourthLine.appendTo(table);
    
    table.appendTo(form);
    f1.appendTo(form);
    f2.appendTo(form);
    f3.appendTo(form);
    f4.appendTo(form);
    f5.appendTo(form);
    f6.appendTo(form);
    fifthLine.appendTo(form);
    form.appendTo(contentDiv);
    contentDiv.appendTo(mainDiv);

    closeImg.appendTo(mainDiv);

    mainDiv.appendTo(jQuery("body"));
    //控制对话框屏幕居中
    jQuery("#messageDialog").css({ "left": (window.screen.availWidth - 370) / 2, "top": (window.screen.availHeight - 300) / 2 });
    //为message对话框添加关闭操作
    jQuery("#closeImg").click(function () {
        Dialog_remove();
    });
    /*评 效果,,通过修改样式来显示不同的星级*/
    jQuery("ul.rating li a").click(function () {
        var title = jQuery(this).attr("title").trim();
        var parentDiv_id = jQuery(this).parent().parent().parent().attr("id");
        //将5个评 结果保存到对应的变量中
        if (parentDiv_id == "complex2") {
            complex = title;
            $("#complex1").val(complex);
        }
        if (parentDiv_id == "atmosphere2") {
            atmosphere = title;
            $("#atmosphere1").val(atmosphere);
        }
        if (parentDiv_id == "service2") {
            service = title;
            $("#service1").val(service);
        }
        if (parentDiv_id == "skill2") {
            skill = title;
            $("#skill1").val(skill);
        }
        if (parentDiv_id == "price2") {
            price = title;
            $("#price1").val(price);
        }
        
        var cl = jQuery(this).parent().attr("class");
        jQuery(this).parent().parent().removeClass().addClass("rating " + cl + "star");
        jQuery(this).blur(); //去掉超链接的虚线框
        return false;
    })
    
    jQuery("#content2").blur(function () {    	
        	var content = jQuery("#content2").val();
        	$("#content1").val(content);
    })
    //为send按钮注册点击事件
    jQuery("#send_message").click(function () {
    	// TODO fix href with routes ...
    	var url = "/myPage/coupon/addComment/" + commentObjId + "/" + commentObjType;
//    	var url = @auth.routes.Comments.addCommentToCoupon(commentObjId, commentObjType);
    	$("#myForm").attr("action", url);
    	if(complex == 0) {
//    		alert("您还没有给第一项打分呢！");
//    		alert("@Messages('comment.noFirst')");
//    		<body data-key="${play.configuration.get("comment.noFirst")}">
    		var key = $('body').data('key');
    		alert(key);
    		
    		return false;
    	}
    	if(atmosphere == 0) {
    		alert("您还没有给第二项打分呢！");
    		return false;
    	}
    	if(service == 0) {
    		alert("您还没有给第三项打分呢！");
    		return false;
    	}
    	if(skill == 0) {
    		alert("您还没有给第四项打分呢！");
    		return false;
    	}
    	if(price == 0) {
    		alert("您还没有给第五项打分呢！");
    		return false;
    	}
    	if($("#content1").val().trim() == "") {
    		alert("请输入评论的内容");
    		return false;
    	}
    
    	
    	$("#myForm").submit();

        Dialog_remove();
    });
    //消息对话框拖动相关操作
    var messageDiv = jQuery("#messageDialog"); //获得消息对话框对象
    var relative_left; //鼠标点下位置到对话框左边框的位置
    var relative_top; //鼠标点下位置到对话框上边框的位置
    //注册鼠标在标题栏上的mousedown事件
    jQuery("#title_bar").mousedown(function (e) {
        relative_left = e.pageX - messageDiv.css("left").substring(0, messageDiv.css("left").length - 2);
        relative_top = e.pageY - messageDiv.css("top").substring(0, messageDiv.css("top").length - 2);
        //注册鼠标移动事件
        jQuery(this).mousemove(function (e) {
            messageDiv.css({ "left": (e.pageX - relative_left) + "px", "top": (e.pageY - relative_top) + "px" });
        });
        jQuery(this).mouseup(function (e) {
            jQuery(this).unbind("mousemove");
        });
    });
}

// 创建遮罩层
function createShadeDiv() { 
    var shadeDiv = jQuery("<div id='shadeDiv' style='position: absolute;z-index: 100;top: 0px;left: 0px;background-color:#000;filter: alpha(opacity=70);-moz-opacity: 0.7;opacity: 0.7;'></div>");
    shadeDiv.appendTo(jQuery("body"));
    document.getElementById('shadeDiv').ondblclick = function () { Dialog_remove(); };
    //取客户端左上坐标，宽，高
    var scrollLeft = (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft);
    var scrollTop = (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop);

    var clientWidth;
    if (window.innerWidth) {
        clientWidth = window.innerWidth;
        //clientWidth = ((Sys.Browser.agent === Sys.Browser.Safari) ? window.innerWidth : Math.min(window.innerWidth, document.documentElement.clientWidth));
    }
    else {
        clientWidth = document.body.clientWidth;
    }

    var clientHeight;
    if (window.innerHeight) {
        clientHeight = window.innerHeight;
        //clientHeight = ((Sys.Browser.agent === Sys.Browser.Safari) ? window.innerHeight : Math.min(window.innerHeight, document.documentElement.clientHeight));
    }
    else {
        clientHeight = document.body.clientHeight;
    }

    var bo = document.getElementById('shadeDiv');
    bo.style.left = scrollLeft + 'px';
    bo.style.top = scrollTop + 'px';
    bo.style.width = clientWidth + 'px';
    bo.style.height = clientHeight + 'px';
    bo.style.display = "";
}
//移除消息对话框
function Dialog_remove() {
    window.onscroll = null;
    window.onresize = null;
    jQuery("#shadeDiv").remove();
    jQuery("#messageDialog").remove();
}
