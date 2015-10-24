'use strict';

/* Directives */


angular.module('myApp.directives', ['simpleLogin'])

  .directive('appVersion', ['version', function(version) {
    return function(scope, elm) {
      elm.text(version);
    };
  }])

  /**
   * A directive that shows elements only when user is logged in.
   */
  .directive('ngShowAuth', ['simpleLogin', '$timeout', function (simpleLogin, $timeout) {
    var isLoggedIn;
    simpleLogin.watch(function(user) {
      isLoggedIn = !!user;
    });

    return {
      restrict: 'A',
      link: function(scope, el) {
        el.addClass('ng-cloak'); // hide until we process it

        function update() {
          // sometimes if ngCloak exists on same element, they argue, so make sure that
          // this one always runs last for reliability
          $timeout(function () {
            el.toggleClass('ng-cloak', !isLoggedIn);
          }, 0);
        }

        update();
        simpleLogin.watch(update, scope);
      }
    };
  }])

  .directive('selectOnClick', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.on('click', function () {
                this.select();
            });
        }
    };
})

  /**
   * A directive that shows elements only when user is logged out.
   */
  .directive('ngHideAuth', ['simpleLogin', '$timeout', function (simpleLogin, $timeout) {
    var isLoggedIn;
    simpleLogin.watch(function(user) {
      isLoggedIn = !!user;
    });

    return {
      restrict: 'A',
      link: function(scope, el) {
        function update() {
          el.addClass('ng-cloak'); // hide until we process it

          // sometimes if ngCloak exists on same element, they argue, so make sure that
          // this one always runs last for reliability
          $timeout(function () {
            el.toggleClass('ng-cloak', isLoggedIn !== false);
          }, 0);
        }

        update();
        simpleLogin.watch(update, scope);
      }
    };
  }])

  .directive('focus', function($timeout) {
    return {
      link: function(scope, element, attrs) {
        element[0].focus();
      }
    };
  })

  .directive('compile', ['$compile', function ($compile) {
  return function(scope, element, attrs) {
    scope.$watch(
      function(scope) {
        return scope.$eval(attrs.compile);
      },
      function(value) {
        element.html(value);
        $compile(element.contents())(scope);
      }
   )};
  }])

  // .directive('scrollGlue', function(){
  //       return {
  //           priority: 1,
  //           require: ['?ngModel'],
  //           restrict: 'A',
  //           link: function(scope, $el, attrs, ctrls){
  //               var el = $el[0],
  //                   ngModel = ctrls[0] || fakeNgModel(true);

  //               function scrollToBottom(){
  //                   el.scrollTop = el.scrollHeight;
  //               }

  //               function shouldActivateAutoScroll(){
  //                   // + 1 catches off by one errors in chrome
  //                   return el.scrollTop + el.clientHeight + 1 >= el.scrollHeight;
  //               }

  //               scope.$watch(function(){
  //                   if(ngModel.$viewValue){
  //                       scrollToBottom();
  //                   }
  //               });

  //               $el.bind('scroll', function(){
  //                   var activate = shouldActivateAutoScroll();
  //                   if(activate !== ngModel.$viewValue){
  //                       scope.$apply(ngModel.$setViewValue.bind(ngModel, activate));
  //                   }
  //               });
  //           }
  //       };
  //   })

  ;
