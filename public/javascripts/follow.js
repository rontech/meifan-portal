  function addFollow(followId, followObjType) {
      var date = new Date().getTime().toString();
	jsRoutes.controllers.auth.MyFollows.addFollow(followId, followObjType,date).ajax(ajax1);
  }

  var successFn = function(data) {
	  if(data == "true"){
		  alert("已收藏/关注！");
	  }else{
		  alert("收藏/关注成功！");
	  }
  };
  var errorFn = function(err) {
	  alert("此功能需登录，请先登录！");
	  }
	   
  ajax1 = {
	  success: successFn,
	  error: errorFn
	 }