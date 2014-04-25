function isForbidden(userId){
	alert("***********");
   $.ajax({
     type: 'GET',
     url: '@auth.routes.MyFollows.followedCoupon(userId)',
     /*statusCode: {
                 404: function() {
                   alert('page not found');
                 },

                 401: function() {
                    alert('bad request');
                }
               }*/
   error: function(err){
	   alert(err.status);
   }
   });
}