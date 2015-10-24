'use strict';

/* Controllers */

angular.module('myApp.controllers', ['firebase.utils', 'simpleLogin'])
  .controller('HomeCtrl', ['$scope', 'fbutil', 'user', 'FBURL',
    function($scope, fbutil, user, FBURL) {
      $scope.syncedValue = fbutil.syncObject('syncedValue');
      $scope.user = user;
      $scope.FBURL = FBURL;
    }
  ])

.controller('DeleteConfirmationModal', ['$scope', '$modalInstance',
  function($scope, $modalInstance) {

    $scope.cancel = function() {
      $modalInstance.dismiss("cancel");
    }

    $scope.deleteConfirm = function() {
      $modalInstance.close("delete");
    }

  }
])

.controller('DashboardCtrl', ['$scope', 'fbutil', 'user', '$firebase', '$location', '$modal', 'simpleLogin', '$intercom',
  function($scope, fbutil, user, $firebase, $location, $modal, simpleLogin, $intercom) {

    $intercom.ping();

    $scope.chrome = window.chrome;
    $scope.firefox = /firefox/i.test(navigator.userAgent);

    $scope.user = user;

    $scope.userInfo = fbutil.syncObject('users/' + user.uid);

    $scope.projects = [];

    $scope.userInfo.$loaded().then(function() {

      if (typeof $scope.userInfo.projects === "undefined" || typeof $scope.userInfo.projects.created === "undefined") {
        return;
      }

      var projectIDs = Object.keys($scope.userInfo.projects.created);

      for (var i = 0; i < projectIDs.length; i++) {
        if (projectIDs[i].indexOf('$') == -1) {
          $scope.projects.push({
            'id': projectIDs[i],
            'settings': fbutil.syncObject('projects/' + projectIDs[i] + '/settings')
          });
        }
      }

    });

    function get_gravatar(email, size) {

      email = email.toLowerCase();

      var MD5 = function(s) {
        function L(k, d) {
          return (k << d) | (k >>> (32 - d))
        }

        function K(G, k) {
          var I, d, F, H, x;
          F = (G & 2147483648);
          H = (k & 2147483648);
          I = (G & 1073741824);
          d = (k & 1073741824);
          x = (G & 1073741823) + (k & 1073741823);
          if (I & d) {
            return (x ^ 2147483648 ^ F ^ H)
          }
          if (I | d) {
            if (x & 1073741824) {
              return (x ^ 3221225472 ^ F ^ H)
            } else {
              return (x ^ 1073741824 ^ F ^ H)
            }
          } else {
            return (x ^ F ^ H)
          }
        }

        function r(d, F, k) {
          return (d & F) | ((~d) & k)
        }

        function q(d, F, k) {
          return (d & k) | (F & (~k))
        }

        function p(d, F, k) {
          return (d ^ F ^ k)
        }

        function n(d, F, k) {
          return (F ^ (d | (~k)))
        }

        function u(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(r(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function f(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(q(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function D(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(p(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function t(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(n(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function e(G) {
          var Z;
          var F = G.length;
          var x = F + 8;
          var k = (x - (x % 64)) / 64;
          var I = (k + 1) * 16;
          var aa = Array(I - 1);
          var d = 0;
          var H = 0;
          while (H < F) {
            Z = (H - (H % 4)) / 4;
            d = (H % 4) * 8;
            aa[Z] = (aa[Z] | (G.charCodeAt(H) << d));
            H++
          }
          Z = (H - (H % 4)) / 4;
          d = (H % 4) * 8;
          aa[Z] = aa[Z] | (128 << d);
          aa[I - 2] = F << 3;
          aa[I - 1] = F >>> 29;
          return aa
        }

        function B(x) {
          var k = "",
            F = "",
            G, d;
          for (d = 0; d <= 3; d++) {
            G = (x >>> (d * 8)) & 255;
            F = "0" + G.toString(16);
            k = k + F.substr(F.length - 2, 2)
          }
          return k
        }

        function J(k) {
          k = k.replace(/rn/g, "n");
          var d = "";
          for (var F = 0; F < k.length; F++) {
            var x = k.charCodeAt(F);
            if (x < 128) {
              d += String.fromCharCode(x)
            } else {
              if ((x > 127) && (x < 2048)) {
                d += String.fromCharCode((x >> 6) | 192);
                d += String.fromCharCode((x & 63) | 128)
              } else {
                d += String.fromCharCode((x >> 12) | 224);
                d += String.fromCharCode(((x >> 6) & 63) | 128);
                d += String.fromCharCode((x & 63) | 128)
              }
            }
          }
          return d
        }
        var C = Array();
        var P, h, E, v, g, Y, X, W, V;
        var S = 7,
          Q = 12,
          N = 17,
          M = 22;
        var A = 5,
          z = 9,
          y = 14,
          w = 20;
        var o = 4,
          m = 11,
          l = 16,
          j = 23;
        var U = 6,
          T = 10,
          R = 15,
          O = 21;
        s = J(s);
        C = e(s);
        Y = 1732584193;
        X = 4023233417;
        W = 2562383102;
        V = 271733878;
        for (P = 0; P < C.length; P += 16) {
          h = Y;
          E = X;
          v = W;
          g = V;
          Y = u(Y, X, W, V, C[P + 0], S, 3614090360);
          V = u(V, Y, X, W, C[P + 1], Q, 3905402710);
          W = u(W, V, Y, X, C[P + 2], N, 606105819);
          X = u(X, W, V, Y, C[P + 3], M, 3250441966);
          Y = u(Y, X, W, V, C[P + 4], S, 4118548399);
          V = u(V, Y, X, W, C[P + 5], Q, 1200080426);
          W = u(W, V, Y, X, C[P + 6], N, 2821735955);
          X = u(X, W, V, Y, C[P + 7], M, 4249261313);
          Y = u(Y, X, W, V, C[P + 8], S, 1770035416);
          V = u(V, Y, X, W, C[P + 9], Q, 2336552879);
          W = u(W, V, Y, X, C[P + 10], N, 4294925233);
          X = u(X, W, V, Y, C[P + 11], M, 2304563134);
          Y = u(Y, X, W, V, C[P + 12], S, 1804603682);
          V = u(V, Y, X, W, C[P + 13], Q, 4254626195);
          W = u(W, V, Y, X, C[P + 14], N, 2792965006);
          X = u(X, W, V, Y, C[P + 15], M, 1236535329);
          Y = f(Y, X, W, V, C[P + 1], A, 4129170786);
          V = f(V, Y, X, W, C[P + 6], z, 3225465664);
          W = f(W, V, Y, X, C[P + 11], y, 643717713);
          X = f(X, W, V, Y, C[P + 0], w, 3921069994);
          Y = f(Y, X, W, V, C[P + 5], A, 3593408605);
          V = f(V, Y, X, W, C[P + 10], z, 38016083);
          W = f(W, V, Y, X, C[P + 15], y, 3634488961);
          X = f(X, W, V, Y, C[P + 4], w, 3889429448);
          Y = f(Y, X, W, V, C[P + 9], A, 568446438);
          V = f(V, Y, X, W, C[P + 14], z, 3275163606);
          W = f(W, V, Y, X, C[P + 3], y, 4107603335);
          X = f(X, W, V, Y, C[P + 8], w, 1163531501);
          Y = f(Y, X, W, V, C[P + 13], A, 2850285829);
          V = f(V, Y, X, W, C[P + 2], z, 4243563512);
          W = f(W, V, Y, X, C[P + 7], y, 1735328473);
          X = f(X, W, V, Y, C[P + 12], w, 2368359562);
          Y = D(Y, X, W, V, C[P + 5], o, 4294588738);
          V = D(V, Y, X, W, C[P + 8], m, 2272392833);
          W = D(W, V, Y, X, C[P + 11], l, 1839030562);
          X = D(X, W, V, Y, C[P + 14], j, 4259657740);
          Y = D(Y, X, W, V, C[P + 1], o, 2763975236);
          V = D(V, Y, X, W, C[P + 4], m, 1272893353);
          W = D(W, V, Y, X, C[P + 7], l, 4139469664);
          X = D(X, W, V, Y, C[P + 10], j, 3200236656);
          Y = D(Y, X, W, V, C[P + 13], o, 681279174);
          V = D(V, Y, X, W, C[P + 0], m, 3936430074);
          W = D(W, V, Y, X, C[P + 3], l, 3572445317);
          X = D(X, W, V, Y, C[P + 6], j, 76029189);
          Y = D(Y, X, W, V, C[P + 9], o, 3654602809);
          V = D(V, Y, X, W, C[P + 12], m, 3873151461);
          W = D(W, V, Y, X, C[P + 15], l, 530742520);
          X = D(X, W, V, Y, C[P + 2], j, 3299628645);
          Y = t(Y, X, W, V, C[P + 0], U, 4096336452);
          V = t(V, Y, X, W, C[P + 7], T, 1126891415);
          W = t(W, V, Y, X, C[P + 14], R, 2878612391);
          X = t(X, W, V, Y, C[P + 5], O, 4237533241);
          Y = t(Y, X, W, V, C[P + 12], U, 1700485571);
          V = t(V, Y, X, W, C[P + 3], T, 2399980690);
          W = t(W, V, Y, X, C[P + 10], R, 4293915773);
          X = t(X, W, V, Y, C[P + 1], O, 2240044497);
          Y = t(Y, X, W, V, C[P + 8], U, 1873313359);
          V = t(V, Y, X, W, C[P + 15], T, 4264355552);
          W = t(W, V, Y, X, C[P + 6], R, 2734768916);
          X = t(X, W, V, Y, C[P + 13], O, 1309151649);
          Y = t(Y, X, W, V, C[P + 4], U, 4149444226);
          V = t(V, Y, X, W, C[P + 11], T, 3174756917);
          W = t(W, V, Y, X, C[P + 2], R, 718787259);
          X = t(X, W, V, Y, C[P + 9], O, 3951481745);
          Y = K(Y, h);
          X = K(X, E);
          W = K(W, v);
          V = K(V, g)
        }
        var i = B(Y) + B(X) + B(W) + B(V);
        return i.toLowerCase()
      };

      var size = size || 80;

      return 'https://www.gravatar.com/avatar/' + MD5(email) + '.jpg?s=' + size + '&d=identicon';
    }

    switch ($scope.user.provider) {
      case "password":
        $scope.userPic = get_gravatar($scope.user.email);
        break;
      case "github":
        $scope.userPic = $scope.user.thirdPartyUserData.avatar_url;
        break;
      case "facebook":
        $scope.userPic = $scope.user.thirdPartyUserData.picture.data.url;
        break;
      case "google":
        $scope.userPic = $scope.user.thirdPartyUserData.picture;
        break;
      case "twitter":
        $scope.userPic = $scope.user.thirdPartyUserData.profile_image_url_https;
        break;
    }

    $scope.openGoPro = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/gopro.html',
        controller: 'GoPro',
        resolve: {
          'user': function() {
            return $scope.user;
          },
          'reason': function() {
            return 'nothing'
          }
        }
      });

      modalInstance.result.then(function(res) {
        if (res == "login") {
          $scope.openLogin();
        }
      });
    }

    var modelist = ace.require("ace/ext/modelist");
    $scope.modeList = modelist;

    // var popularModes = ['C_Cpp', 'CSS', 'HTML', 'Java', 'JavaScript', 'ObjectiveC', 'PHP', 'Python', 'Ruby'];
    // $scope.popularModes = {};
    // for (var i=0;i<popularModes.length;i++){
    //   $scope.popularModes[popularModes[i]] = modelist.modesByName[popularModes[i]];
    // }

    $scope.getModeName = function(mode) {
      if (mode) {
        return modelist.modesByName[mode.replace('ace/mode/', '')].caption;
      }
    }

    $scope.deleteFile = function(id) {

      var modalInstance = $modal.open({
        templateUrl: 'partials/confirmdelete.html',
        controller: 'DeleteConfirmationModal'
      });

      modalInstance.result.then(function(res) {
        if (res == "delete") {

          for (var i = 0; i < $scope.projects.length; i++) {
            if ($scope.projects[i].id == id) {
              $scope.projects[i].settings.$destroy();
              $scope.projects.splice(i, 1);
            }
          }

          $firebase(fbutil.ref('projects/' + id)).$remove();
          $firebase(fbutil.ref('users/' + user.uid + '/projects/created/' + id)).$remove();

        }
      });

    }

    $scope.contactUs = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/feedbackmodal.html',
        controller: 'FeedbackModal'
      });
    }

    $scope.logout = function() {

      $scope.user = null;
      user = null;
      if ($scope.userInfo) {
        $scope.userInfo.$destroy();
      }

      simpleLogin.logout();

      $location.path('/');

    }

    $scope.openMyAccount = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/myaccount.html',
        controller: 'MyAccountCtrl'
      });
    }

    $scope.newFile = function() {

      if ($scope.projects.length > 0) {
        console.log("ADDITIONAL FILE CREATED");
        ga('send', 'event', 'additionalfile', 'create', 'new');
      }

      $location.path('/c');
    }

    $scope.goHome = function() {
      $location.path("/");
    }

  }
])

.controller('CreateProjectCtrl', ['$scope', 'fbutil', 'user', 'FBURL', '$firebase', '$location', '$intercom',
  function($scope, fbutil, user, FBURL, $firebase, $location, $intercom) {
    var projectsRef = $firebase(fbutil.ref('projects'));

    var addMe = {
      'settings': {
        'mode': 'ace/mode/text',
        'tabs': '4',
        'private': false,
        'created': Firebase.ServerValue.TIMESTAMP
      }
    }

    function createProject(){
      projectsRef.$push(addMe).then(function(ref) {
        var pid = ref.name();
        console.log("PID", pid);

        if (user) {
          var usersProjectsRef = $firebase(fbutil.ref('users/' + user.uid + '/projects/created/' + pid));
          usersProjectsRef.$set(true);
        }

        var addProject = $firebase(fbutil.ref('stats/projects/' + pid));
        addProject.$set(true);

        ga('send', 'event', 'file', 'create', 'new');

        $intercom.event("create-file");

        $location.path('/e/' + pid).replace();
      });
    }

    if (user) {
      addMe.settings.creator = user.uid;

      var userInfo = fbutil.syncObject('users/' + user.uid);

      userInfo.$loaded().then(function(){

        if (typeof userInfo.private.editorSettings.tabs !== "undefined"){
          addMe.settings.tabs = userInfo.private.editorSettings.tabs;
        }

        userInfo.$destroy();

        createProject();

      });

    }else{
      createProject();
    }

  }
])

.controller('RegistrationsCtrl', ['$scope', 'fbutil',
  function($scope, fbutil) {

    $scope.registrations = fbutil.syncObject('usernames');

    $scope.num = 0;

    $scope.pro = fbutil.syncObject('stats/promemberships');

    $scope.pronum = 0;
    $scope.prodollars = 0;

    $scope.projects = fbutil.syncObject('stats/projects');

    $scope.projectsnum = 0;

    $scope.projects.$watch(function() {
      $scope.projectsnum = Object.keys($scope.projects).length - 3;
    })

    $scope.pro.$watch(function() {
      $scope.pronum = Object.keys($scope.pro).length - 4 + 114;
      $scope.prodollars = $scope.pronum * 19;
    })

    $scope.registrations.$watch(function() {
      $scope.num = Object.keys($scope.registrations).length - 16;
    })

  }
])

.controller('ProResponse', ['$scope', 'fbutil', '$firebase', '$modalInstance', '$modal', 'success',
  function($scope, fbutil, $firebase, $modalInstance, $modal, success) {

    $scope.success = success;

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

    // $scope.tryAgain = function(){
    //   var modalInstance = $modal.open({
    //     templateUrl: 'partials/gopro.html',
    //     controller: 'GoPro',
    //     resolve: {
    //       'user': function(){
    //         return $scope.user;
    //       },
    //       'reason': function(){
    //         return 'nothing'
    //       }
    //     }
    //   });

    //   modalInstance.result.then(function(res){
    //     if (res=="login"){
    //       $scope.openLogin();
    //     }
    //   });
    //   $modalInstance.dismiss('cancel');
    // }

  }
])

.controller('GoPro', ['$scope', 'fbutil', '$firebase', 'simpleLogin', '$modalInstance', '$http', '$intercom', '$modal', 'user', 'reason',
  function($scope, fbutil, $firebase, simpleLogin, $modalInstance, $http, $intercom, $modal, user, reason) {

    $scope.reason = reason;
    $scope.user = user;

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

    $scope.openLogin = function() {

      $modalInstance.close('login');

    }

    function success() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/proresponse.html',
        controller: 'ProResponse',
        resolve: {
          'success': function() {
            return true
          }
        }
      });
    }

    function failed() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/proresponse.html',
        controller: 'ProResponse',
        resolve: {
          'success': function() {
            return false
          }
        }
      });
    }

    $scope.openMonthlyStripe = function() {

      var handler = StripeCheckout.configure({
        key: 'pk_live_4ZpE6CchSsYeB4D6jkBmy2kI',
        image: 'https://mattkremer.com/wp-content/uploads/2014/08/Kobra_logo_512x512_black.png',
        token: function(token) {
          // Use the token to create the charge with a server-side script.
          // You can access the token ID with `token.id`

          $http.post('https://ss.kobra.io/stripecheckout', {
            'stripeToken': token.id,
            'subscription': 'monthly',
            'uid': $scope.user.uid
          }).success(function(data) {
            if (!data['success']) {
              failed();
            } else {
              ga('send', 'event', 'register', 'pro', 'monthly');
              $intercom.event("upgrade-monthly");
              success();
            }
          }).error(function(data, status, headers, config) {
            failed();
          })

        },
        allowRememberMe: false
      });

      handler.open({
        name: 'Kobra.io Membership',
        description: 'Monthly at $4.99',
        amount: 499
      });

      $modalInstance.dismiss('cancel');

    };

    $scope.openYearlyStripe = function() {

      var handler = StripeCheckout.configure({
        key: 'pk_live_4ZpE6CchSsYeB4D6jkBmy2kI',
        image: 'https://mattkremer.com/wp-content/uploads/2014/08/Kobra_logo_512x512_black.png',
        token: function(token) {
          // Use the token to create the charge with a server-side script.
          // You can access the token ID with `token.id`

          console.log("Sending checkout...");

          $http.post('https://ss.kobra.io/stripecheckout', {
            'stripeToken': token.id,
            'subscription': 'yearly',
            'uid': $scope.user.uid
          }).success(function(data) {
            console.log("RESPONSE", data);
            if (!data['success']) {
              failed();
            } else {
              ga('send', 'event', 'register', 'pro', 'yearly');
              $intercom.event("upgrade-yearly");
              success();
            }
          }).error(function(data, status, headers, config) {
            failed();
          })

        },
        allowRememberMe: false
      });

      handler.open({
        name: 'Kobra.io Membership',
        description: 'Yearly at $38',
        amount: 3800
      });

      $modalInstance.dismiss('cancel');

    };

  }
])

.controller('NoAccess', ['$scope', '$modalInstance', 'fbutil', 'user', 'pid',
  function($scope, $modalInstance, fbutil, user, pid) {

    $scope.user = user;

    $scope.data = {
      'password': ''
    }

    $scope.checkPassword = function(){
      var inFile = fbutil.syncObject('projects/' + pid + '/inFile');

    }

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

    $scope.openLogin = function() {
      $modalInstance.close('login');
    }

  }
])

.controller('SimpleModal', ['$scope', '$modalInstance', 'message', 'title',
  function($scope, $modalInstance, message, title) {

    $scope.message = message;
    $scope.title = title;

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

  }
])

.controller('ConfirmNewFileCtrl', ['$scope', '$modalInstance',
  function($scope, $modalInstance) {

    $scope.cancel = function() {
      $modalInstance.close('cancel');
    };

    $scope.openLogin = function() {
      $modalInstance.close("login");
    }

  }
])

.controller('MainCtrl', ['$scope', '$location', '$modal', 'fbutil', 'simpleLogin', 'user', '$intercom',
  function($scope, $location, $modal, fbutil, simpleLogin, user, $intercom) {

    if (user) {
      $location.path('/dashboard');
    }

    $scope.chrome = window.chrome;
    $scope.firefox = /firefox/i.test(navigator.userAgent);

    $scope.newFile = function() {
      if ($scope.user) {
        $location.path('/c');
      } else {
        var modalInstance = $modal.open({
          templateUrl: 'partials/confirmnewfile.html',
          controller: 'ConfirmNewFileCtrl'
        });

        modalInstance.result.then(function(res) {
          if (res == "login") {
            $scope.openLogin();
          } else {
            $location.path('/c');
          }
        })

      }
    }

    $scope.user = user;

    $scope.userInfo = null;

    if ($scope.user) {
      $scope.userInfo = fbutil.syncObject('users/' + user.uid);
    }

    $scope.contactUs = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/feedbackmodal.html',
        controller: 'FeedbackModal'
      });
    }

    $scope.goPro = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/gopro.html',
        controller: 'GoPro',
        resolve: {
          'user': function() {
            return $scope.user;
          },
          'reason': function() {
            return 'nothing'
          }
        }
      });

      modalInstance.result.then(function(res) {
        if (res == "login") {
          $scope.openLogin();
        }
      });
    }

    $scope.openLogin = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/login.html',
        controller: 'LoginModal'
      });

      modalInstance.result.then(function(user) {

        $scope.user = user;

        if ($scope.userInfo) {
          $scope.userInfo.$destroy();
        }

        $scope.userInfo = fbutil.syncObject('users/' + user.uid);

        $location.path("/dashboard");

      });

    }

    $scope.logout = function() {

      $scope.user = null;
      user = null;
      if ($scope.userInfo) {
        $scope.userInfo.$destroy();
      }

      simpleLogin.logout();

    }

  }
])

.controller('MyAccountCtrl', ['$scope', 'fbutil', 'simpleLogin', '$firebase', '$modalInstance',
  function($scope, fbutil, simpleLogin, $firebase, $modalInstance) {

    $scope.user = simpleLogin.user;

    console.log($scope.user);

    $scope.userInfo = fbutil.syncObject('users/' + $scope.user.uid);

    $scope.data = {
      'changepass': {
        'error': '',
        'success': '',
        'old': '',
        'new1': '',
        'new2': ''
      }
    }

    $scope.changePassword = function() {
      $scope.data.changepass.error = "";
      $scope.data.changepass.success = "";
      if ($scope.data.changepass.new1 != $scope.data.changepass.new2) {
        $scope.data.changepass.error = "The passwords you entered were different."
        return;
      }

      simpleLogin.changePassword($scope.user.email, $scope.data.changepass.old, $scope.data.changepass.new1).then(function() {

        $scope.data.changepass.old = "";
        $scope.data.changepass.new1 = "";
        $scope.data.changepass.new2 = "";

        $scope.data.changepass.success = "Your password has been changed!";

      }, function(err) {

        $scope.data.changepass.error = err;

      })

    }

    $scope.cancel = function() {
      $modalInstance.close('cancel');
    };

  }
])

.controller('EditorCtrl', ['$scope', 'fbutil', 'user', 'FBURL', '$firebase', '$routeParams', '$modal', 'simpleLogin', '$timeout', "$location", "$sce", "$intercom",
  function($scope, fbutil, user, FBURL, $firebase, $routeParams, $modal, simpleLogin, $timeout, $location, $sce, $intercom) {

    $intercom.ping();

    var pid = $routeParams.pid;

    $scope.chrome = window.chrome;
    $scope.firefox = /firefox/i.test(navigator.userAgent);

    var isEditor = false,
      initMode = false;

    $scope.user = user;

    $scope.userInfo = false;

    $scope.pid = pid;

    $scope.username = "User " + (Math.floor(Math.random() * 9000) + 1000);

    if (user) {
      $scope.userInfo = fbutil.syncObject('users/' + user.uid);
      $scope.userInfo.$loaded().then(function() {
        $scope.username = $scope.userInfo.public.username;
      })
    }


    /* Navigation Function */

    $scope.openMyDashboard = function() {
      $location.path('/dashboard');
    }

    $scope.contactUs = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/feedbackmodal.html',
        controller: 'FeedbackModal'
      });
    }

    $scope.goHome = function() {
      $location.path("/");
    }

    $scope.openMyAccount = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/myaccount.html',
        controller: 'MyAccountCtrl'
      });
    }

    $scope.openGoPro = function() {

      var modalInstance = $modal.open({
        templateUrl: 'partials/gopro.html',
        controller: 'GoPro',
        resolve: {
          'user': function() {
            return $scope.user;
          },
          'reason': function() {
            return 'nothing'
          }
        }
      });

      modalInstance.result.then(function(res) {
        if (res == "login") {
          $scope.openLogin();
        }
      });

    }

    $scope.openLogin = function() {
      var modalInstance = $modal.open({
        templateUrl: 'partials/login.html',
        controller: 'LoginModal'
      });

      modalInstance.result.then(function(user) {

        $scope.user = user;

        if ($scope.userInfo) {
          $scope.userInfo.$destroy();
        }
        $scope.userInfo = fbutil.syncObject('users/' + user.uid);

        $scope.userInfo.$loaded().then(function() {
          $scope.username = $scope.userInfo.public.username;
          var res = updateUserRef();
          $scope.firepad.setUserColor("#" + res.color);
          // $firebase(myInFileRef).$set({
          //   username: $scope.username
          // });
        })

        $scope.options = fbutil.syncObject('users/' + user.uid + '/private/editorSettings');

        $scope.options.$loaded().then(function() {
          initializeOptions();
        });

      });

    }

    $scope.logout = function() {
      $scope.user = null;
      user = null;
      if ($scope.userInfo) {
        $scope.userInfo.$destroy();
      }

      $scope.username = "User " + (Math.floor(Math.random() * 9000) + 1000);

      var res = updateUserRef();
      $scope.firepad.setUserColor("#" + res.color);

      // $firebase(myInFileRef).$set({
      //       username: $scope.username
      //     });

      var temp = {
        'theme': $scope.options.theme,
        'keybinding': $scope.options.keybinding,
        'fontSize': $scope.options.fontSize,
        'folding': $scope.options.folding,
        'invisibles': $scope.options.invisibles,
        'indentGuides': $scope.options.indentGuides,
        'gutter': $scope.options.gutter,
        'highlightSelected': $scope.options.highlightSelected,
        'fadeFold': $scope.options.fadeFold,
        'scrollPastEnd': $scope.options.scrollPastEnd,
        'wordWrap': $scope.options.wordWrap
      }

      $scope.options.$destroy();

      $scope.options = temp;

      simpleLogin.logout();
    }

    /* End Navigation Functions */

    /* Copy to Clipboard */

    $scope.justCopied = false;

    $timeout(function() {

      ZeroClipboard.config({
        forceHandCursor: true
      });

      var clipboard = new ZeroClipboard(document.getElementById("copytoclipboard"));

      clipboard.on("ready", function(readyEvent) {
        console.log("ZeroClipboard SWF is ready!");

        clipboard.on("aftercopy", function(event) {
          $scope.$apply(function() {
            $scope.justCopied = true;
            $timeout(function() {
              $scope.justCopied = false;
            }, 4000);
          })
        });
      });

    }, 10);

    /* End Copy to Clipboard */

    /* Text Chat */

    $scope.nextMessage = "";

    $scope.messages = fbutil.syncObject('projects/' + pid + '/messages');

    $scope.messages.$watch(function() {
      setTimeout(function() {

        var el = document.getElementById('messages');

        el.scrollTop = el.scrollHeight * 2;
      }, 10)
    })

    $scope.messagesRef = $firebase(fbutil.ref('projects/' + pid + '/messages'));


    $scope.checkEnter = function($event) {
      if ($event.keyCode == 13) {
        if ($scope.nextMessage == "") {
          $event.stopPropagation();
          $event.cancelBubble = true;
          $event.preventDefault();
          return false;
        }
        $event.stopPropagation();
        $event.cancelBubble = true;
        $event.preventDefault();
        $scope.addMessage();
        return false;
      }
    }

    $scope.addMessage = function() {
      var isUser = false,
        ispro = false;
      if ($scope.user) {
        isUser = true;
        if ($scope.userInfo.admin.pro) {
          ispro = true;
        }
      }
      $scope.messagesRef.$push({
        message: $scope.nextMessage,
        username: $scope.username,
        isuser: isUser,
        time: Firebase.ServerValue.TIMESTAMP,
        pro: ispro
      });
      $scope.nextMessage = "";
    }

    /* End Text Chat */

    /* Users in File & User List */

    var myInFileRef = false;

    $scope.inFile = fbutil.syncObject('projects/' + pid + '/inFile');

    $scope.usersInFile = 0;
    var addedCSSRules = {};

    function addStyleRule(css) {
      var styleElement;
      if (typeof document === "undefined" || document === null) {
        return;
      }

      styleElement = document.createElement('style');
      document.documentElement.getElementsByTagName('head')[0].appendChild(styleElement);

      for (var i = 0; i < css.length; i++) {

        styleElement.sheet.insertRule(css[i], i);

      }

    };

    function hsl2hex(h, s, l) {
      if (s === 0) {
        return rgb2hex(l, l, l);
      }
      var var2 = l < 0.5 ? l * (1 + s) : (l + s) - (s * l);
      var var1 = 2 * l - var2;
      var hue2rgb = function(hue) {
        if (hue < 0) {
          hue += 1;
        }
        if (hue > 1) {
          hue -= 1;
        }
        if (6 * hue < 1) {
          return var1 + (var2 - var1) * 6 * hue;
        }
        if (2 * hue < 1) {
          return var2;
        }
        if (3 * hue < 2) {
          return var1 + (var2 - var1) * 6 * (2 / 3 - hue);
        }
        return var1;
      };
      return rgb2hex(hue2rgb(h + 1 / 3), hue2rgb(h), hue2rgb(h - 1 / 3));
    }

    function rgb2hex(r, g, b) {
      function digits(n) {
        var m = Math.round(255 * n).toString(16);
        return m.length === 1 ? '0' + m : m;
      }
      return '#' + digits(r) + digits(g) + digits(b);
    }

    function colorFromUserId(userId) {
      var a = 1;
      for (var i = 0; i < userId.length; i++) {
        a = 17 * (a + userId.charCodeAt(i)) % 360;
      }
      var hue = a / 360;

      return hsl2hex(hue, 1, 0.85);
    }

    function get_gravatar(email, size) {

      email = email.toLowerCase();

      var MD5 = function(s) {
        function L(k, d) {
          return (k << d) | (k >>> (32 - d))
        }

        function K(G, k) {
          var I, d, F, H, x;
          F = (G & 2147483648);
          H = (k & 2147483648);
          I = (G & 1073741824);
          d = (k & 1073741824);
          x = (G & 1073741823) + (k & 1073741823);
          if (I & d) {
            return (x ^ 2147483648 ^ F ^ H)
          }
          if (I | d) {
            if (x & 1073741824) {
              return (x ^ 3221225472 ^ F ^ H)
            } else {
              return (x ^ 1073741824 ^ F ^ H)
            }
          } else {
            return (x ^ F ^ H)
          }
        }

        function r(d, F, k) {
          return (d & F) | ((~d) & k)
        }

        function q(d, F, k) {
          return (d & k) | (F & (~k))
        }

        function p(d, F, k) {
          return (d ^ F ^ k)
        }

        function n(d, F, k) {
          return (F ^ (d | (~k)))
        }

        function u(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(r(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function f(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(q(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function D(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(p(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function t(G, F, aa, Z, k, H, I) {
          G = K(G, K(K(n(F, aa, Z), k), I));
          return K(L(G, H), F)
        }

        function e(G) {
          var Z;
          var F = G.length;
          var x = F + 8;
          var k = (x - (x % 64)) / 64;
          var I = (k + 1) * 16;
          var aa = Array(I - 1);
          var d = 0;
          var H = 0;
          while (H < F) {
            Z = (H - (H % 4)) / 4;
            d = (H % 4) * 8;
            aa[Z] = (aa[Z] | (G.charCodeAt(H) << d));
            H++
          }
          Z = (H - (H % 4)) / 4;
          d = (H % 4) * 8;
          aa[Z] = aa[Z] | (128 << d);
          aa[I - 2] = F << 3;
          aa[I - 1] = F >>> 29;
          return aa
        }

        function B(x) {
          var k = "",
            F = "",
            G, d;
          for (d = 0; d <= 3; d++) {
            G = (x >>> (d * 8)) & 255;
            F = "0" + G.toString(16);
            k = k + F.substr(F.length - 2, 2)
          }
          return k
        }

        function J(k) {
          k = k.replace(/rn/g, "n");
          var d = "";
          for (var F = 0; F < k.length; F++) {
            var x = k.charCodeAt(F);
            if (x < 128) {
              d += String.fromCharCode(x)
            } else {
              if ((x > 127) && (x < 2048)) {
                d += String.fromCharCode((x >> 6) | 192);
                d += String.fromCharCode((x & 63) | 128)
              } else {
                d += String.fromCharCode((x >> 12) | 224);
                d += String.fromCharCode(((x >> 6) & 63) | 128);
                d += String.fromCharCode((x & 63) | 128)
              }
            }
          }
          return d
        }
        var C = Array();
        var P, h, E, v, g, Y, X, W, V;
        var S = 7,
          Q = 12,
          N = 17,
          M = 22;
        var A = 5,
          z = 9,
          y = 14,
          w = 20;
        var o = 4,
          m = 11,
          l = 16,
          j = 23;
        var U = 6,
          T = 10,
          R = 15,
          O = 21;
        s = J(s);
        C = e(s);
        Y = 1732584193;
        X = 4023233417;
        W = 2562383102;
        V = 271733878;
        for (P = 0; P < C.length; P += 16) {
          h = Y;
          E = X;
          v = W;
          g = V;
          Y = u(Y, X, W, V, C[P + 0], S, 3614090360);
          V = u(V, Y, X, W, C[P + 1], Q, 3905402710);
          W = u(W, V, Y, X, C[P + 2], N, 606105819);
          X = u(X, W, V, Y, C[P + 3], M, 3250441966);
          Y = u(Y, X, W, V, C[P + 4], S, 4118548399);
          V = u(V, Y, X, W, C[P + 5], Q, 1200080426);
          W = u(W, V, Y, X, C[P + 6], N, 2821735955);
          X = u(X, W, V, Y, C[P + 7], M, 4249261313);
          Y = u(Y, X, W, V, C[P + 8], S, 1770035416);
          V = u(V, Y, X, W, C[P + 9], Q, 2336552879);
          W = u(W, V, Y, X, C[P + 10], N, 4294925233);
          X = u(X, W, V, Y, C[P + 11], M, 2304563134);
          Y = u(Y, X, W, V, C[P + 12], S, 1804603682);
          V = u(V, Y, X, W, C[P + 13], Q, 4254626195);
          W = u(W, V, Y, X, C[P + 14], N, 2792965006);
          X = u(X, W, V, Y, C[P + 15], M, 1236535329);
          Y = f(Y, X, W, V, C[P + 1], A, 4129170786);
          V = f(V, Y, X, W, C[P + 6], z, 3225465664);
          W = f(W, V, Y, X, C[P + 11], y, 643717713);
          X = f(X, W, V, Y, C[P + 0], w, 3921069994);
          Y = f(Y, X, W, V, C[P + 5], A, 3593408605);
          V = f(V, Y, X, W, C[P + 10], z, 38016083);
          W = f(W, V, Y, X, C[P + 15], y, 3634488961);
          X = f(X, W, V, Y, C[P + 4], w, 3889429448);
          Y = f(Y, X, W, V, C[P + 9], A, 568446438);
          V = f(V, Y, X, W, C[P + 14], z, 3275163606);
          W = f(W, V, Y, X, C[P + 3], y, 4107603335);
          X = f(X, W, V, Y, C[P + 8], w, 1163531501);
          Y = f(Y, X, W, V, C[P + 13], A, 2850285829);
          V = f(V, Y, X, W, C[P + 2], z, 4243563512);
          W = f(W, V, Y, X, C[P + 7], y, 1735328473);
          X = f(X, W, V, Y, C[P + 12], w, 2368359562);
          Y = D(Y, X, W, V, C[P + 5], o, 4294588738);
          V = D(V, Y, X, W, C[P + 8], m, 2272392833);
          W = D(W, V, Y, X, C[P + 11], l, 1839030562);
          X = D(X, W, V, Y, C[P + 14], j, 4259657740);
          Y = D(Y, X, W, V, C[P + 1], o, 2763975236);
          V = D(V, Y, X, W, C[P + 4], m, 1272893353);
          W = D(W, V, Y, X, C[P + 7], l, 4139469664);
          X = D(X, W, V, Y, C[P + 10], j, 3200236656);
          Y = D(Y, X, W, V, C[P + 13], o, 681279174);
          V = D(V, Y, X, W, C[P + 0], m, 3936430074);
          W = D(W, V, Y, X, C[P + 3], l, 3572445317);
          X = D(X, W, V, Y, C[P + 6], j, 76029189);
          Y = D(Y, X, W, V, C[P + 9], o, 3654602809);
          V = D(V, Y, X, W, C[P + 12], m, 3873151461);
          W = D(W, V, Y, X, C[P + 15], l, 530742520);
          X = D(X, W, V, Y, C[P + 2], j, 3299628645);
          Y = t(Y, X, W, V, C[P + 0], U, 4096336452);
          V = t(V, Y, X, W, C[P + 7], T, 1126891415);
          W = t(W, V, Y, X, C[P + 14], R, 2878612391);
          X = t(X, W, V, Y, C[P + 5], O, 4237533241);
          Y = t(Y, X, W, V, C[P + 12], U, 1700485571);
          V = t(V, Y, X, W, C[P + 3], T, 2399980690);
          W = t(W, V, Y, X, C[P + 10], R, 4293915773);
          X = t(X, W, V, Y, C[P + 1], O, 2240044497);
          Y = t(Y, X, W, V, C[P + 8], U, 1873313359);
          V = t(V, Y, X, W, C[P + 15], T, 4264355552);
          W = t(W, V, Y, X, C[P + 6], R, 2734768916);
          X = t(X, W, V, Y, C[P + 13], O, 1309151649);
          Y = t(Y, X, W, V, C[P + 4], U, 4149444226);
          V = t(V, Y, X, W, C[P + 11], T, 3174756917);
          W = t(W, V, Y, X, C[P + 2], R, 718787259);
          X = t(X, W, V, Y, C[P + 9], O, 3951481745);
          Y = K(Y, h);
          X = K(X, E);
          W = K(W, v);
          V = K(V, g)
        }
        var i = B(Y) + B(X) + B(W) + B(V);
        return i.toLowerCase()
      };

      var size = size || 80;

      return 'https://www.gravatar.com/avatar/' + MD5(email) + '.jpg?s=' + size + '&d=identicon';
    }

    $scope.userPic = "";

    function updateUserRef(retJSON) {

      var build = {
        color: colorFromUserId($scope.myInFileRefId).replace("#", ""),
        pro: false,
        inVideo: $scope.videoData.videoChatOn
      }
      if ($scope.user) {
        build.username = $scope.userInfo.public.username;
        if ($scope.userInfo.admin.pro) {
          build.pro = true;
        }
        switch ($scope.user.provider) {
          case "password":
            build.picture = get_gravatar($scope.user.email);
            break;
          case "github":
            build.picture = $scope.user.thirdPartyUserData.avatar_url;
            break;
          case "facebook":
            build.picture = $scope.user.thirdPartyUserData.picture.data.url;
            break;
          case "google":
            build.picture = $scope.user.thirdPartyUserData.picture;
            break;
          case "twitter":
            build.picture = $scope.user.thirdPartyUserData.profile_image_url_https;
            break;
        }
      } else {
        build.username = $scope.username;
        build.picture = get_gravatar($scope.myInFileRefId);
      }

      $scope.userPic = build.picture;

      console.log("UPDATEUSERREF", $scope.myInFileRefId, build.color);

      console.log(build);

      if (typeof retJSON === "undefined" || retJSON == false) {
        $firebase(myInFileRef).$set(build);
      }

      return build;

    }

    $scope.inFile.$loaded().then(function() {

      var keys = Object.keys($scope.inFile),
        numUsers = 0,
        invitee = "";

      for (var i = 0; i < keys.length; i++) {
        if (keys[i].indexOf('$') != 0) {
          if ($scope.myInFileRefId == "" || $scope.myInFileRefId != keys[i]) {
            invitee = $scope.inFile[keys[i]].username;
            numUsers += 1;
          }
        }
      }

      if (numUsers > 0) {
        if (invitee !== "" && numUsers == 1){
          ga('send', 'event', 'invitee', invitee, $scope.username);
        }
        ga('send', 'event', 'collab', $scope.username, numUsers);
      }

    })

    $scope.inFile.$watch(function() {

      var keys = Object.keys($scope.inFile),
        numUsers = 0,
        prefix = "";

      if ($scope.chrome) {
        prefix = "-webkit-";
      }

      if ($scope.firefox) {
        prefix = "-moz-";
      }

      for (var i = 0; i < keys.length; i++) {
        if (keys[i].indexOf('$') != 0) {
          numUsers += 1;

          var color = colorFromUserId(keys[i]).replace("#", "");
          var otherName = $scope.inFile[keys[i]].username;

          if (!(keys[i] in addedCSSRules) || (otherName != addedCSSRules[keys[i]])) {
            addStyleRule(["@" + prefix + "keyframes fadeOut" + keys[i] + " {\
              0%  {\
                opacity: 1;\
              }\
              90% {\
                opacity: 1;\
              }\
              99% {\
                opacity: 0;\
              }\
              100% {\
                opacity: 0;\
              }\
            }", ".other-client-selection-" + color + "{opacity:0.3;}",".other-client-cursor-" + color + ":after{content: \"" + otherName + "\";opacity:0;position:absolute;top:-20px;background-color:#" + color + ";padding:3px;z-index:1000;left:-2px;font-weight:bold;color:#444;" + prefix + "animation: fadeOut" + keys[i] + " 2s linear backwards;}"])
            addedCSSRules[keys[i]] = otherName;
          }

        }
      }

      $scope.usersInFile = numUsers - 1;

    })

    $scope.myInFileRefId = "";
    var waitingOnFileRef = false;

    $scope.inFile.$loaded().then(function() {

      var inFileRef = $firebase(fbutil.ref("projects/" + pid + "/inFile"));

      inFileRef.$push({
        temp: true
      }).then(function(ref) {
        var myid = ref.name();
        $scope.myInFileRefId = ref.name();
        myInFileRef = fbutil.ref("projects/" + pid + "/inFile/" + myid);
        myInFileRef.onDisconnect().remove();
        updateUserRef();
        if (waitingOnFileRef) {
          initializeEditor();
        }
      });

    }).catch(function(err) {

      // You don't have permission to view this file.

      var modalInstance = $modal.open({
        templateUrl: 'partials/noaccess.html',
        controller: 'NoAccess',
        resolve: {
          'user': function() {
            return $scope.user;
          },
          'pid': function(){
            return $scope.pid;
          }
        }
      });

      modalInstance.result.then(function(res) {
        if (res == "login") {
          $scope.openLogin();
        }
      });

    });

    /* End Users in File & User List */

    /* Video Chat */

    $scope.videoData = {
      'videoChatOn': false,
      'userCount': 0,
      'myid': '',
      'videos': [],
      'currentVideo': '',
      'streams': {},
      'initializing': {},
      'startingVideo': false
    }

    var myVideoRef = null;

    var inVideoChat = fbutil.syncObject('projects/' + pid + '/videoChat');

    inVideoChat.$watch(function() {
      console.log("VC", inVideoChat, Object.keys(inVideoChat));
    })

    $scope.setCurrentVideo = function(easyrtcid) {
      $scope.videoData.currentVideo = easyrtcid;
      var currentVideo = document.getElementById('currentVideo');
      easyrtc.setVideoObjectSrc(currentVideo, $scope.videoData.streams[easyrtcid]);
    }

    $scope.leaveVideo = function() {
      if ($scope.videoData.videoChatOn) {
        easyrtc.leaveRoom(pid, function() {
          $scope.$apply(function() {
            easyrtc.disconnect();
            $scope.videoData.videoChatOn = false;
            $scope.videoData.userCount = 0;
            $scope.videoData.myid = "";
            $scope.videoData.videos = [];
            $scope.videoData.currentVideo = '';
            $scope.videoData.streams = {};
            $scope.videoData.startingVideo = false;
            updateUserRef();
            var myVideo = document.getElementById('myVideo');
            easyrtc.setVideoObjectSrc(myVideo, null);
            easyrtc.getLocalStream().stop();

            myVideoRef.remove();

          });
        });
      }
    }

    $scope.joinVideo = function() {

      if ($scope.videoData.startingVideo) {
        return;
      }

      var users = 0,
        proUsers = 0;

      var keys = Object.keys(inVideoChat);

      for (var i = 0; i < keys.length; i++) {
        if (keys[i].indexOf('$') != 0) {
          users += 1;
          if (inVideoChat[keys[i]].pro) {
            proUsers += 1;
          }
        }
      }

      if (users == 2 && (proUsers < 2 || !$scope.user || !$scope.userInfo.admin.pro)) {

        if (users == proUsers) {

          var modalInstance = $modal.open({
            templateUrl: 'partials/gopro.html',
            controller: 'GoPro',
            resolve: {
              'user': function() {
                return $scope.user;
              },
              'reason': function() {
                return 'videochat'
              }
            }
          });

          modalInstance.result.then(function(res) {
            if (res == "login") {
              $scope.openLogin();
            }
          });

        } else {

          if (!$scope.user || !$scope.userInfo.admin.pro) {

            var modalInstance = $modal.open({
              templateUrl: 'partials/gopro.html',
              controller: 'GoPro',
              resolve: {
                'user': function() {
                  return $scope.user;
                },
                'reason': function() {
                  return 'videochat'
                }
              }
            });

            modalInstance.result.then(function(res) {
              if (res == "login") {
                $scope.openLogin();
              }
            });

          } else {

            var modalInstance = $modal.open({
              templateUrl: 'partials/simplemodal.html',
              controller: 'SimpleModal',
              resolve: {
                'message': function() {
                  return 'There are already 2 people in Video Chat, and not all of them have PRO memberships. Let them know in "Text Chat" that they need to get PRO to chat with 3 or more people!';
                },
                'title': function() {
                  return 'Video Chat Has Non-PRO Users';
                }
              }
            });

          }

        }

        return;
      }

      $scope.videoData.startingVideo = true;

      easyrtc.setIceUsedInCalls({
        "iceServers": [{
          "url": "stun:stun.l.google.com:19302"
        }, {
          "url": "turn:ss.kobra.io:3478",
          "username": "kobra",
          "credential": "likeakobra"
        }]
      });

      easyrtc.setSocketUrl("https://ss.kobra.io:8080");

      easyrtc.setStreamAcceptor(function(easyrtcid, stream) {

        if ($scope.videoData.videos.indexOf(easyrtcid) == -1 && easyrtcid !== $scope.videoData.myid) {
          $scope.videoData.userCount += 1;
          $scope.videoData.videos.push(easyrtcid);


          $scope.videoData.streams[easyrtcid] = stream;
          $scope.$apply();

          var element = document.getElementById(easyrtcid + "-video");

          easyrtc.setVideoObjectSrc(element, stream);

          if ($scope.videoData.userCount == 1) {
            $scope.videoData.currentVideo = easyrtcid;
            var currentVideo = document.getElementById('currentVideo');
            easyrtc.setVideoObjectSrc(currentVideo, stream);
            $scope.$apply();
          }

          if ($scope.videoData.initializing[easyrtcid] == true) {
            $scope.videoData.initializing[easyrtcid] = false;
          }

        }

      });

      easyrtc.setOnStreamClosed(function(easyrtcid) {
        // $timeout(function(){
        $scope.$apply(function() {
          if (easyrtcid == $scope.videoData.myid) {
            return;
          }
          $scope.videoData.userCount -= 1;
          $scope.videoData.videos.splice($scope.videoData.videos.indexOf(easyrtcid), 1);
          if ($scope.videoData.currentVideo == easyrtcid) {
            if ($scope.videoData.userCount > 0) {
              $scope.setCurrent($scope.videoData.videos[0]);
            } else {
              $scope.videoData.currentVideo = "";
            }
          }
        })
        // });

      });

      easyrtc.initMediaSource(function() {
        easyrtc.connect(pid, function(easyrtcid) {
          var selfVideo = document.getElementById("myVideo");
          easyrtc.setVideoObjectSrc(selfVideo, easyrtc.getLocalStream());
          easyrtc.muteVideoObject(selfVideo, true);

          easyrtc.joinRoom(pid, null, function() {
            $scope.videoData.myid = easyrtc.myEasyrtcid;
            $scope.videoData.videoChatOn = true;
            $scope.$apply();

            updateUserRef();

            var inVideoRef = $firebase(fbutil.ref("projects/" + pid + "/videoChat"));

            var addMe = {
              pro: false
            };
            if ($scope.user) {
              if ($scope.userInfo.admin.pro) {
                addMe.pro = true;
              }
            }

            inVideoRef.$push(addMe).then(function(ref) {
              var vidid = ref.name();
              myVideoRef = fbutil.ref("projects/" + pid + "/videoChat/" + vidid);
              myVideoRef.onDisconnect().remove();
            });

            easyrtc.setRoomOccupantListener(function(roomName, list, selfInfo) {
              // $timeout(function(){
              if (!roomName == pid) {
                return;
              }
              console.log(list);
              for (var key in list) {
                var u = list[key];
                if (u.roomJoinTime > selfInfo.roomJoinTime) {
                  if ($scope.videoData.videos.indexOf(u.easyrtcid) == -1) {
                    if (typeof $scope.videoData.initializing[u.easyrtcid] == "undefined") {
                      $scope.videoData.initializing[u.easyrtcid] = true;
                      easyrtc.call(u.easyrtcid);
                    } else if ($scope.videoData.initializing[u.easyrtcid] == false) {
                      $scope.videoData.initializing[u.easyrtcid] = true;
                      easyrtc.call(u.easyrtcid);
                    }

                  }
                  // if (document.getElementById(easyrtc.idToName(u['easyrtcid'])+'-video') == null){
                  //   easyrtc.call(u.easyrtcid);
                  // }
                }
              }
              // }, 1);

            })
          }, function() {
            // console.log("Failed to join room!");
          });
        }, function() {
          //console.log("No media source.");
        })
      }, function(errorText) {
        // console.log("failed...")
      })

    }

    /* End Video Chat */

    $scope.setPrivate = function() {
      console.log("SetPrivate");
      if ($scope.projectSettings.private) {

        var modalInstance = $modal.open({
          templateUrl: 'partials/inviteUsers.html',
          controller: 'InviteUsers',
          resolve: {
            'pid': function() {
              return pid;
            }
          }
        });

      } else {

        if ($scope.user) {
          if ($scope.userInfo.admin.pro) {

            $scope.projectSettings.private = true;
            $scope.projectSettings.$save();

            var modalInstance = $modal.open({
              templateUrl: 'partials/inviteUsers.html',
              controller: 'InviteUsers',
              resolve: {
                'pid': function() {
                  return pid;
                }
              }
            });

          } else {
            var modalInstance = $modal.open({
              templateUrl: 'partials/gopro.html',
              controller: 'GoPro',
              resolve: {
                'user': function() {
                  return $scope.user;
                },
                'reason': function() {
                  return 'makeprivate'
                }
              }
            });

            modalInstance.result.then(function(res) {
              if (res == "login") {
                $scope.openLogin();
              }
            });

          }
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'partials/gopro.html',
            controller: 'GoPro',
            resolve: {
              'user': function() {
                return $scope.user;
              },
              'reason': function() {
                return 'makeprivate'
              }
            }
          });

          modalInstance.result.then(function(res) {
            if (res == "login") {
              $scope.openLogin();
            }
          });
        }
      }
    }



    $scope.availableThemes = ace.require('ace/ext/themelist');

    if ($scope.user) {

      $scope.options = fbutil.syncObject('users/' + user.uid + '/private/editorSettings');

      $scope.options.$loaded().then(function() {

        initializeEditor();

      });

    } else {
      $scope.options = {
        'theme': 'ace/theme/monokai',
        'keybinding': 'ace',
        'fontSize': '12px',
        'folding': 'markbegin',
        'invisibles': false,
        'indentGuides': true,
        'gutter': true,
        'highlightSelected': true,
        'fadeFold': false,
        'scrollPastEnd': false,
        'wordWrap': false,
        'tabs': '4'
      }
    }

    $scope.currentMode = "ace/mode/text";

    var modelist = ace.require("ace/ext/modelist");
    $scope.modeList = modelist;

    var popularModes = ['c_cpp', 'css', 'html', 'java', 'javascript', 'objectivec', 'php', 'python', 'ruby'];
    $scope.popularModes = {};
    for (var i=0;i<popularModes.length;i++){
      $scope.popularModes[popularModes[i]] = modelist.modesByName[popularModes[i]];
    }

    $scope.fileName = "";

    $scope.projectSettings = fbutil.syncObject('projects/' + pid + '/settings');

    $scope.projectSettings.$loaded().then(function() {

      $scope.fileName = $scope.projectSettings.filename;

      if ($scope.projectSettings.cliConnected){

        // var cliSocket = io('https://ss.kobra.io');

        var cliSocket = io.connect("https://ss.kobra.io", {'transports': ['websocket', 'polling']});

        $scope.cliMessages = [];
        $scope.cliConsoleInput = "";

        // $scope.focusConsole = function(){
        //   document.getElementById('consoleInput').focus();
        // }

        $scope.checkConsoleEnter = function($event) {
          if ($event.keyCode == 13) {
            if ($scope.cliConsoleInput == "") {
              $event.stopPropagation();
              $event.cancelBubble = true;
              $event.preventDefault();
              return false;
            }
            $event.stopPropagation();
            $event.cancelBubble = true;
            $event.preventDefault();

              cliSocket.emit('input', {
                'fileid': pid,
                'input': $scope.cliConsoleInput
              });

              $scope.cliMessages.push($scope.cliConsoleInput);

              $scope.cliConsoleInput = "";            

            return false;
          }
        }

        cliSocket.emit('watch', {
          'fileid': pid
        });

        cliSocket.on('stdout', function(data){
          // console.log("STDOUT", data, ansi2html(data.line));

          $scope.$apply(function(){
            $scope.cliMessages.push($sce.trustAsHtml(ansi_up.ansi_to_html(data.line).replace('\n','<br />')));

            setTimeout(function() {

              var el = document.getElementById('cliMessages');

              el.scrollTop = el.scrollHeight * 2;
            }, 10)

          });

        });

        $scope.saveCLI = function(){

          console.log("Emitting save.");

          cliSocket.emit('update', {
            'fileid': pid,
            'contents': $scope.firepad.getText()
          });

        }

      }

      $scope.$watch("fileName", function() {
        if (typeof $scope.fileName === "undefined") {
          return;
        }

        if (isEditor) {
          var modelist = ace.require("ace/ext/modelist");
          var getMode = modelist.getModeForPath($scope.fileName);

          if (getMode.mode !== "ace/mode/text") {
            $scope.setMode(getMode.mode);
          }
        }

        $scope.projectSettings.filename = $scope.fileName;
        $scope.projectSettings.$save();
      });

      $scope.currentMode = $scope.projectSettings.mode;
    });


    var unwatchProjectSettings = $scope.projectSettings.$watch(function() {
      if ($scope.projectSettings.mode != $scope.currentMode) {
        $scope.currentMode = $scope.projectSettings.mode;
        if (isEditor) {
          $scope.setMode($scope.currentMode);
        } else {
          initMode = true;
        }
      }
      if ($scope.projectSettings.tabs != $scope.options.tabs){
        if (isEditor){
          $scope.setTabs($scope.projectSettings.tabs, true);
        }
      }
    });

    /* Preferences & Settings */

    var keybindings = {
      ace: null,
      vim: ace.require("ace/keyboard/vim").handler,
      emacs: "ace/keyboard/emacs"
    }

    $scope.dropdownMode = "";

    $scope.$watch('dropdownMode', function() {
      if ($scope.dropdownMode == "") {
        return;
      }

      $scope.setMode($scope.dropdownMode);

    })

    $scope.setTabs = function(tab, projectOveride){
      if (typeof tab == "undefined"){
        tab = "4";
      }
      if (typeof projectOveride == "undefined" || projectOveride == false){
        $scope.options.tabs = tab;
        if ($scope.user){
          $scope.options.$save();
        }
      }
      if ($scope.projectSettings.tabs != tab){
        $scope.projectSettings.tabs = tab;
        $scope.projectSettings.$save();
      }
      if (tab == 'tab'){
        $scope.editor.getSession().setUseSoftTabs(false);
      }else{
        tab = parseInt(tab);
        $scope.editor.getSession().setUseSoftTabs(true);
        $scope.editor.getSession().setTabSize(tab);
      }
    }

    $scope.setMode = function(mode, $event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.currentMode = mode;
      $scope.dropdownMode = mode;
      if ($scope.projectSettings.mode != $scope.currentMode){
        $scope.projectSettings.mode = $scope.currentMode;
        $scope.projectSettings.$save();
      }
      $scope.editor.getSession().setMode($scope.currentMode);
    }

    $scope.toggleWordWrap = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.wordWrap = !$scope.options.wordWrap;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.getSession().setUseWrapMode($scope.options.wordWrap);
    }

    $scope.toggleScrollPastEnd = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.scrollPastEnd = !$scope.options.scrollPastEnd;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setOption("scrollPastEnd", $scope.options.scrollPastEnd);
    }

    $scope.toggleFadeFold = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.fadeFold = !$scope.options.fadeFold;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setFadeFoldWidgets($scope.options.fadeFold);
    }

    $scope.toggleHighlightSelected = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.highlightSelected = !$scope.options.highlightSelected;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setHighlightSelectedWord($scope.options.highlightSelected);
    }

    $scope.toggleGutter = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.gutter = !$scope.options.gutter;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.renderer.setShowGutter($scope.options.gutter);
    }

    $scope.toggleIndentGuides = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.indentGuides = !$scope.options.indentGuides;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setDisplayIndentGuides($scope.options.indentGuides);
    }

    $scope.toggleInvisibles = function($event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.invisibles = !$scope.options.invisibles;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setShowInvisibles($scope.options.invisibles);
    }

    $scope.setFolding = function(folding, $event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.folding = folding;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.getSession().setFoldStyle($scope.options.folding);
    }

    $scope.setFontSize = function(size, $event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.fontSize = size;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.container.style.fontSize = $scope.options.fontSize;
    }

    $scope.setKeybinding = function(type, $event) {
      if ($event) {
        $event.stopPropagation();
      }
      $scope.options.keybinding = type;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setKeyboardHandler(keybindings[$scope.options.keybinding]);
    }

    $scope.setTheme = function(theme, $event) {
      if ($event) {
        $event.stopPropagation();
      }
      console.log(theme, $scope.editor);
      $scope.options.theme = theme;
      if ($scope.user) {
        $scope.options.$save();
      }
      $scope.editor.setTheme(theme, function() {

      });
      return false;
    }

    $scope.aceGetKeyboardShortcuts = function() {
      "use strict";

      var editor = $scope.editor;

      var commands = editor.commands.byName;

      var commandName;
      var key;
      var platform = editor.commands.platform;
      $scope.editorPlatform = platform;
      var kb = {};
      for (commandName in commands) {
        try {
          key = commands[commandName].bindKey[platform];
          if (key) {
            kb[commandName] = key;
          }
        } catch (e) {
          kb[commandName] = "None";
          // errors on properties without bindKey we don't want them
          // so the errors don't need handling.
        }
      }

      $scope.keyboardShortcuts = kb;
    }

    $scope.execCommand = function(cmd) {

      $scope.editor.commands.exec(cmd, $scope.editor);
      var textInput = document.getElementById('firepad').getElementsByClassName('ace_text-input')[0];
      textInput.focus();

    }

    $scope.menuHoverOpen = function(menu) {
      var ids = ['file', 'edit', 'selection', 'find', 'goto', 'settings'];
      var open = '';
      for (var i = 0; i < ids.length; i++) {
        if (document.getElementById('header-menu-' + ids[i]).className.indexOf('open') > -1) {
          open = ids[i];
          break;
        }
      }
      if (open == menu) {
        return;
      }
      if (open !== '') {
        var newEl = document.getElementById('header-menu-' + menu);
        var childrens = newEl.children;
        for (var i = 0; i < childrens.length; i++) {
          if (childrens[i].className.indexOf('dropdown-toggle') > -1)
            childrens[i].click();
        }
      }
    }

    function initializeOptions() {

      if ($scope.projectSettings.getMode){
        $scope.setMode(modelist.getModeForPath($scope.fileName).mode);
        $scope.projectSettings.getMode = false;
        $scope.projectSettings.$save();
      }else{
        if (initMode) $scope.setMode($scope.currentMode);
      }
      $scope.editor.getSession().setUseWrapMode($scope.options.wordWrap);
      $scope.editor.setOption("scrollPastEnd", $scope.options.scrollPastEnd);
      $scope.editor.setFadeFoldWidgets($scope.options.fadeFold);
      $scope.editor.setHighlightSelectedWord($scope.options.highlightSelected);
      $scope.editor.renderer.setShowGutter($scope.options.gutter);
      $scope.editor.setDisplayIndentGuides($scope.options.indentGuides);
      $scope.editor.setShowInvisibles($scope.options.invisibles);
      $scope.editor.getSession().setFoldStyle($scope.options.folding);
      $scope.editor.container.style.fontSize = $scope.options.fontSize;
      $scope.editor.setKeyboardHandler(keybindings[$scope.options.keybinding]);
      $scope.setTheme($scope.options.theme);
      if (typeof $scope.projectSettings.tabs !== "undefined"){
        $scope.setTabs($scope.projectSettings.tabs, true);
      }else{
        $scope.setTabs($scope.options.tabs);
      }
    }

    /* End Preferences & Settings */

    /* Set Up Code Editor */

    $scope.firepad = null;

    function initializeEditor() {

      if ($scope.myInFileRefId == "") {
        waitingOnFileRef = true;
        return;
      }

      var firepadRef = fbutil.ref('projects/' + pid + '/firepad');

      var editor = ace.edit("firepad");

      $scope.editor = editor;

      ace.require("ace/ext/language_tools");
      // var modelist = ace.require("ace/ext/modelist");

      // = modelist;

      initializeOptions();

      var firepad = Firepad.fromACE(firepadRef, editor, {
        defaultText: 'Get Started!\n\n    1. Paste your code, then name it on the right, or drag & drop a file in.\n\n    2. Share your link! (https://kobra.io/#/e/' + pid + ')',
        userId: $scope.myInFileRefId
      });

      $scope.firepad = firepad;

      $scope.aceGetKeyboardShortcuts();

      isEditor = true;

      // Setup drop target

      function handleFileSelect(evt) {
        console.log("STARTED");
        evt.stopPropagation();
        evt.preventDefault();

        var files = evt.dataTransfer.files;
        var file = files[0];

        var reader = new FileReader();

        $scope.$apply(function() {
          $scope.fileName = file.name;
        });

        reader.onloadend = function(evt) {
          if (evt.target.readyState == FileReader.DONE) {
            firepad.setText(evt.target.result);
          }
        }

        var blob = file.slice(0, file.size - 1);
        reader.readAsBinaryString(blob);

      }

      function handleDragOver(evt) {
        evt.stopPropagation();
        evt.preventDefault();
        evt.dataTransfer.dropEffect = 'copy';
      }

      var el = document.getElementById('firepad');
      el.addEventListener('dragover', handleDragOver, false);
      el.addEventListener('drop', handleFileSelect, false);

    }

    if (!user) {
      initializeEditor();
    }

    /* End Set Up Code Editor */

  }
])

.controller('InviteUsers', ['$scope', '$modalInstance', 'fbutil', '$firebase', '$timeout', 'simpleLogin', 'pid',
  function($scope, $modalInstance, fbutil, $firebase, $timeout, simpleLogin, pid) {

    $scope.userExists = false;

    $scope.data = {
      addUsername: ""
    }

    $scope.users = fbutil.syncObject('projects/' + pid + '/users');

    $scope.checkName = function() {

      $scope.userExists = false;

      $timeout(function() {

        if ($scope.data.addUsername == "") {
          console.log("Empty");
          return;
        }

        console.log('usernames/' + $scope.data.addUsername.toLowerCase());

        var checkUsername = fbutil.syncObject('usernames/' + $scope.data.addUsername.toLowerCase());

        checkUsername.$loaded().then(function() {

          console.log("CHECKING", checkUsername);

          if ('taken' in checkUsername) {
            $scope.userExists = true;
          } else {
            $scope.userExists = false;
          }
          // checkUsername.$destroy();
        });
      }, 1);

    };

    $scope.addUser = function() {
      if ($scope.data.addUsername == "") {
        return;
      }

      var getUser = fbutil.syncObject('usernames/' + $scope.data.addUsername.toLowerCase());

      getUser.$loaded().then(function() {

        var addingUser = $firebase(fbutil.ref('projects/' + pid + '/users/' + getUser.uid));

        addingUser.$set({
          'username': $scope.data.addUsername
        });

        $scope.data.addUsername = "";

      })

    }

    $scope.removeUser = function(uid) {

      console.log("Removing", uid);

      var getUser = $firebase(fbutil.ref('projects/' + pid + '/users/' + uid));

      getUser.$remove();

    }

    $scope.makePublic = function() {

      var getSettings = $firebase(fbutil.ref('projects/' + pid + '/settings/private'));

      getSettings.$set(false);

      $scope.cancel();

    }

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

  }
])

.controller('FeedbackModal', ['$scope', '$http', '$modalInstance', '$modal',
  function($scope, $http, $modalInstance, $modal) {

    $scope.data = {
      'from': '',
      'subject': '',
      'message': '',
      'error': ''
    }

    $scope.sendMail = function() {
      $scope.data.error = "";
      if ($scope.data.from == "" || $scope.data.subject == "" || $scope.data.message == "") {

        $scope.data.error = "All fields are required.";

        return;
      }
      $http.post('https://ss.kobra.io/sendemail', {
        'from': $scope.data.from,
        'subject': $scope.data.subject,
        'message': $scope.data.message
      }).success(function(data) {
        if (data['success']) {
          var modalInstance = $modal.open({
            templateUrl: 'partials/simplemodal.html',
            controller: 'SimpleModal',
            resolve: {
              'message': function() {
                return "Thanks for contacting us! We'll get back to you as soon as possible.";
              },
              'title': function() {
                return 'Message Sent';
              }
            }
          });
          $scope.cancel();
        } else {

          $scope.data.error = "There was a problem sending your message.";

        }
      })
    }

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

  }
])

.controller('LoginModal', ['$scope', 'simpleLogin', '$modalInstance', '$q', 'fbutil', '$timeout', '$firebase', '$http', '$modal', '$intercom',
  function($scope, simpleLogin, $modalInstance, $q, fbutil, $timeout, $firebase, $http, $modal, $intercom) {

    $scope.reg = {
      username: '',
      email: '',
      pass1: '',
      pass2: ''
    }

    $scope.login = {
      email: '',
      password: ''
    }

    $scope.reset = {
      email: ''
    }

    $scope.spinner = {
      resetPassword: false,
      login: false,
      loginGithub: false,
      loginFacebook: false,
      loginTwitter: false,
      loginGoogle: false,
      registerEmail: false,
      registerGithub: false,
      registerFacebook: false,
      registerTwitter: false,
      registerGoogle: false
    }

    $scope.invalidUsername = false;

    $scope.regError = "";
    $scope.loginError = "";
    $scope.resetError = "";
    $scope.resetSuccess = "";

    $scope.resetPassword = function() {
      $scope.spinner.resetPassword = true;
      $scope.resetError = "";
      $scope.resetSuccess = "";
      if ($scope.reset.email !== "") {
        simpleLogin.resetPassword($scope.reset.email).then(function() {
          resetSuccess();
          $scope.spinner.resetPassword = false;
        }, function(err) {
          console.log(err);
          $scope.spinner.resetPassword = false;
          resetError("You must enter the email address you used to sign up for Kobra.");
        })
      } else {
        $scope.spinner.resetPassword = false;
        resetError("You must enter the email address you used to sign up for Kobra.");
      }
    }

    function ensureRegistration(uid) {
      var deferred = $q.defer();

      $timeout(function() {

        var checkCreated = fbutil.syncObject('users/' + uid);

        checkCreated.$loaded().then(function() {

          if ('private' in checkCreated) {
            deferred.resolve(true);
          } else {
            deferred.resolve(false);
          }

          checkCreated.$destroy();

        });

      }, 1);

      return deferred.promise;
    }

    function validateUsernameNoErrors() {
      return validateUsername(false);
    }

    function validateUsername(showTextError) {
      var deferred = $q.defer();

      if (typeof showTextError === "undefined") {
        showTextError = false;
      }

      console.log("Top", showTextError);

      $timeout(function() {

        var error = false,
          errorMsg = '';

        if ($scope.reg.username == "") {
          error = true;
          errorMsg = "You must specify a Username."
        } else {
          if (/[^a-zA-Z0-9]/.test($scope.reg.username.toLowerCase())) {
            error = true;
            errorMsg = "Username can only contain alphanumeric characters (a-z, A-Z, 0-9)."
          }
        }

        if (error) {
          if (showTextError) regError(errorMsg);
          $scope.invalidUsername = true;
          deferred.resolve(false);
        }

        if (!error) {

          var checkUsername = fbutil.syncObject('usernames/' + $scope.reg.username.toLowerCase());

          checkUsername.$loaded().then(function() {
            if ('taken' in checkUsername) {
              checkUsername.$destroy();
              console.log("Bottom", showTextError);
              if (showTextError) regError("That username is taken.");
              $scope.invalidUsername = true;
              deferred.resolve(false);
            } else {
              checkUsername.$destroy();
              $scope.invalidUsername = false;
              deferred.resolve(true);
            }
          });

        }

      }, 1);

      return deferred.promise
    }

    $scope.$watch('reg.username', validateUsernameNoErrors)

    function validateEmailPass() {
      if ($scope.reg.email == "") {
        regError("You must enter a valid email address.");
        $scope.spinner.registerEmail = false;
        return false;
      }
      if ($scope.reg.pass1 != $scope.reg.pass2) {
        regError("Your password fields don't match.");
        $scope.spinner.registerEmail = false;
        return false;
      }
      if ($scope.reg.pass1 == "") {
        regError("You must enter a password.");
        $scope.spinner.registerEmail = false;
        return false;
      }
      return true;
    }

    var errorMsgs = {
      "EMAIL_TAKEN": "This email address is already in use.",
      "INVALID_EMAIL": "The email address you entered is invalid.",
      "INVALID_PASSWORD": "That email and password combination doesn't exist.",
      "INVALID_USER": "That email and password combination doesn't exist.",
      "USER_DENIED": "Authorization was denied."
    }

    function loginError(err) {
      if (err in errorMsgs) {
        $scope.loginError = errorMsgs[err];
      } else {
        $scope.loginError = err;
      }
    }

    function regError(err) {
      if (err in errorMsgs) {
        $scope.regError = errorMsgs[err];
      } else {
        $scope.regError = err;
      }
    }

    function resetError(err) {
      if (err in errorMsgs) {
        $scope.resetError = errorMsgs[err];
      } else {
        $scope.resetError = err;
      }
    }

    function resetSuccess() {
      $scope.resetSuccess = "Your password was reset!<br />Please check your email for your new password.";
    }

    function setupUserEntries(username, email, uid) {
      var usernameRef = $firebase(fbutil.ref('usernames/' + username.toLowerCase()));
      usernameRef.$set({
        'taken': true,
        'uid': uid
      });

      $http.post('https://ss.kobra.io/checkpropayment', {
        'email': email,
        'uid': uid
      }).success(function(data) {
        if (data['success']) {
          var modalInstance = $modal.open({
            templateUrl: 'partials/simplemodal.html',
            controller: 'SimpleModal',
            resolve: {
              'message': function() {
                return "Thanks for pre-ordering Kobra.io! We've added your Pro membership to your account.";
              },
              'title': function() {
                return 'PRO Membership Added';
              }
            }
          });
        }
      })

      var userRef = $firebase(fbutil.ref('users/' + uid));
      userRef.$set({
        'private': {
          'email': email,
          'created': Firebase.ServerValue.TIMESTAMP,
          'editorSettings': {
            'theme': 'ace/theme/monokai',
            'keybinding': 'ace',
            'fontSize': '12px',
            'folding': 'markbegin',
            'invisibles': false,
            'indentGuides': true,
            'gutter': true,
            'highlightSelected': true,
            'fadeFold': false,
            'scrollPastEnd': false,
            'wordWrap': false,
            'tabs': 'tab'
          }
        },
        'public': {
          'username': username
        },
        'admin': {
          'pro': false
        }
      })
    }

    $scope.register = function(method){

      var okayUsername = "",
        okayLogin = "",
        regemail = "",
        reguid = "",
        reguser = null;

      $scope.regError = "";

      function checkFinished(){

        if (okayUsername !== "" && okayLogin !== ""){

          if (okayUsername && okayLogin){
            // Register Success
            console.log("register success");
            setupUserEntries($scope.reg.username, regemail, reguid);
            ga('send', 'event', 'register', 'registered', method);
            $scope.signedIn(reguser);
          }else{
            // Register Failure
            console.log("register failure");
            if (okayLogin) simpleLogin.logout();
          }

          spinner(false);

        }

      }

      function usernameCallback(check){
        if (check){
          // Username is okay.
          okayUsername = true;
        }else{
          // Username is not okay.
          okayUsername = false;
        }
        checkFinished();
      }

      validateUsername(true).then(usernameCallback);

      function errorCallback(err){
        okayLogin = false;
        if (typeof err !== "undefined") regError(err['code']);
        checkFinished();
      }

      function successCallback(user, email, uid){

        var checkUser = fbutil.syncObject('users/' + uid);

        checkUser.$loaded().then(function(){
          if ('public' in checkUser) {
            okayLogin = false;
            regError("You've already registered with that login service.");
            checkFinished();
          }else{
            reguser = user;
            regemail = email;
            reguid = uid;
            okayLogin = true;
            checkFinished();
          }
          checkUser.$destroy();
        }, errorCallback);

      }

      function spinner(on){

        switch(method){
          case "email":
            $scope.spinner.registerEmail = on;
            break;
          case "github":
            $scope.spinner.registerGithub = on;
            break;
          case "facebook":
            $scope.spinner.registerFacebook = on;
            break;
          case "twitter":
            $scope.spinner.registerTwitter = on;
            break;
          case "google":
            $scope.spinner.registerGoogle = on;
            break;
        }

      }

      spinner(true);

      switch(method){
        case "email":
          if (validateEmailPass()){
            simpleLogin.createUser($scope.reg.email, $scope.reg.pass1).then(function(user) {
              simpleLogin.login('password', {
                email: $scope.reg.email,
                password: $scope.reg.pass1,
                rememberMe: true
              }).then(function(user) {
                successCallback(user, $scope.reg.email, user.uid);
              }, errorCallback);
            }, errorCallback);

          }else{
            errorCallback();
          }
          break;
        case "github":
          simpleLogin.login('github',{
            rememberMe: true,
            scope: 'user'
          }).then(function(user){
            
            var email = "";
            if (user.thirdPartyUserData.email) {
              email = user.thirdPartyUserData.email;
            } else {
              var emails = user.thirdPartyUserData.emails;
              for (var i = 0; i < emails.length; i++) {
                if (emails[i].primary) {
                  email = emails[i].email;
                }
              }
            }

            successCallback(user, email, user.uid);

          }, errorCallback);
          break;
        case "facebook":
          simpleLogin.login('facebook', {
            rememberMe: true,
            scope: 'email'
          }).then(function(user) {

            successCallback(user, user.thirdPartyUserData.email, user.uid);

          }, errorCallback)
          break;
        case "twitter":
          simpleLogin.login('twitter', {
            rememberMe: true
          }).then(function(user) {

            successCallback(user, false, user.uid);

          }, errorCallback);
          break;
        case "google":
          simpleLogin.login('google', {
            rememberMe: true
          }).then(function(user) {

            successCallback(user, user.email, user.uid);

          }, errorCallback)
          break;
      }

    }

    $scope.loginEmail = function() {
      $scope.loginError = "";
      $scope.spinner.login = true;
      simpleLogin.login('password', {
        email: $scope.login.email,
        password: $scope.login.password,
        rememberMe: true
      }).then(function(user) {
        $scope.signedIn(user);
        $scope.spinner.login = false;
      }, function(err) {
        loginError(err["code"]);
        $scope.spinner.login = false;
      })
    }

    $scope.loginGithub = function() {
      $scope.spinner.loginGithub = true;
      $scope.loginError = "";
      simpleLogin.login('github', {
        rememberMe: true,
        scope: 'user'
      }).then(function(user) {

        ensureRegistration(user.uid).then(function(check) {
          if (check) {
            $scope.signedIn(user);
            $scope.spinner.loginGithub = false;
          } else {
            loginError('You must <a class="alert-link" style="cursor:pointer;" ng-click="page=' + "'register'" + '">register an account</a> to login.');
            simpleLogin.logout();
            $scope.spinner.loginGithub = false;
          }
        });

      }, function(err) {
        $scope.spinner.loginGithub = false;
        loginError(err['code']);
      })

    }

    $scope.loginFacebook = function() {
      $scope.spinner.loginFacebook = true;
      $scope.loginError = "";
      simpleLogin.login('facebook', {
        rememberMe: true,
        scope: 'email'
      }).then(function(user) {

        ensureRegistration(user.uid).then(function(check) {
          if (check) {
            $scope.signedIn(user);
            $scope.spinner.loginFacebook = false;
          } else {
            loginError('You must <a class="alert-link" style="cursor:pointer;" ng-click="page=' + "'register'" + '">register an account</a> to login.');
            simpleLogin.logout();
            $scope.spinner.loginFacebook = false;
          }
        });

      }, function(err) {
        $scope.spinner.loginFacebook = false;
        loginError(err['code']);
      })
    }

    $scope.loginTwitter = function() {
      $scope.spinner.loginTwitter = true;
      $scope.loginError = "";
      simpleLogin.login('twitter', {
        rememberMe: true
      }).then(function(user) {

        ensureRegistration(user.uid).then(function(check) {
          if (check) {
            $scope.signedIn(user);
            // console.log("You're the best!", user);
            $scope.spinner.loginTwitter = false;
          } else {
            loginError('You must <a class="alert-link" style="cursor:pointer;" ng-click="page=' + "'register'" + '">register an account</a> to login.');
            simpleLogin.logout();
            $scope.spinner.loginTwitter = false;
          }
        });

      }, function(err) {
        $scope.spinner.loginTwitter = false;
        loginError(err['code']);
      })
    }

    $scope.loginGoogle = function() {
      $scope.spinner.loginGoogle = true;
      $scope.loginError = "";
      simpleLogin.login('google', {
        rememberMe: true
      }).then(function(user) {

        ensureRegistration(user.uid).then(function(check) {
          if (check) {
            $scope.spinner.loginGoogle = false;
            $scope.signedIn(user);
            // console.log("You're the best!", user);
          } else {
            loginError('You must <a class="alert-link" style="cursor:pointer;" ng-click="page=' + "'register'" + '">register an account</a> to login.');
            simpleLogin.logout();
            $scope.spinner.loginGoogle = false;
          }
        });

      }, function(err) {
        $scope.spinner.loginGoogle = false;
        loginError(err['code']);
      })
    }

    $scope.signedIn = function(user) {
      $modalInstance.close(user);
    }

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

    $scope.page = "login";
    $scope.showEmailPass = false;

  }
])