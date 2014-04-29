function followedCouponIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedCoupon(userId).ajax(ajax1);
}

function followedBlogIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedBlog(userId).ajax(ajax1);
}

function followedStyleIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedStyle(userId).ajax(ajax1);
}

var successFn = function(data) {
};

var errorFn = function(err) {
	if(err.status == 403){
		alert("您和该用户未互相关注，无法查看该内容。");
	}
	  }
	   
ajax1 = {
	  success: successFn,
	  error: errorFn
	 }