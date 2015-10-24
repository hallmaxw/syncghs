(function() {
   'use strict';

   /* Services */

   angular.module('myApp.services', [])

      // put your services here!
      // .service('serviceName', ['dependency', function(dependency) {}]);

     .service('messageList', ['fbutil', function(fbutil) {
       return fbutil.syncArray('messages', {limit: 10, endAt: null});
     }])

     .service('$intercom', ['simpleLogin', 'fbutil', function(simpleLogin, fbutil){

     	var userInfo = {};
     	var user = {};

      var _event = function(name, meta){

        simpleLogin.getUser().then(function(user){

          if (user){

            var t = new Date().getTime();

            if (typeof meta === "undefined"){
              window.Intercom('trackEvent', name);
            }else{
              window.Intercom('trackEvent', name, meta);
            }

            console.info("Intercom Event: "+name);

          }

        });

      }

     	var _pingIntercom = function(){

     		simpleLogin.getUser().then(function(u){

          if (!u){
            console.info("Intercom: No user");
            window.Intercom('shutdown');
            return;
          }

     			user = u;

     			userInfo = fbutil.syncObject('users/' + user.uid);

     			userInfo.$loaded().then(function() {

            if (!userInfo.public) return;

     				window.Intercom('boot', {
  					  app_id: "r4ma7fu1",
  					  name: userInfo.public.username,
  					  email: userInfo.private.email,
  					  user_id: user.uid,
  					  created_at: Math.ceil(userInfo.private.created/1000),
              // signed_up_at: Math.ceil(userInfo.private.created/1000)
  					});

            console.info("Intercom: Boot");
					
     			});

     		});

     	}

      simpleLogin.watch(_pingIntercom);

     	return {
     		ping: _pingIntercom,
        event: _event
     	}

     }])

})();

