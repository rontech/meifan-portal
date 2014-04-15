  function addFollow(followId, followObjType, msg) {
	alert(msg);
	jsRoutes.controllers.auth.MyFollows.addFollow(followId, followObjType).ajax();
  }
