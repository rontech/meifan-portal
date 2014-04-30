function followedCouponIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedCoupon(userId).ajax(ajax2);
}

function followedBlogIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedBlog(userId).ajax(ajax2);
}

function followedStyleIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedStyle(userId).ajax(ajax2);
}

var successFn = function(data) {
};

var errorFn = function(err) {
	if(err.readyState == 4 && err.status == 403){
		alert("您和该用户未互相关注，无法查看该内容。");
	}
	
	if(err.readyState ==0 && err.status ==0){
		alert("无法查看该内容，请确认您已经登录，并且和该用户已互相关注。")
	}
	  }
	   
ajax2 = {
	  success: successFn,
	  error: errorFn
	 }