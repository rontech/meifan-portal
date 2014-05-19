function followedCouponIsForbidden(userId){
	jsRoutes.controllers.auth.MyFollows.followedCoupon(userId).ajax(ajax2);
}

var successFn = function(data) {
};

var errorFn = function(err) {
	if(err.readyState == 4 && err.status == 403){
		alert("您和该用户未互相关注，无法查看该内容。");
	}
	
	if(err.readyState ==4 && err.status ==401){
		alert("无法查看该内容，请您先登录。")
	}
 }
	   
ajax2 = {
      async:false,
	  success: successFn,
	  error: errorFn
	 }