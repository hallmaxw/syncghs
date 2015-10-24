'use strict';

/* Filters */

angular.module('myApp.filters', [])
   .filter('interpolate', ['version', function(version) {
      return function(text) {
         return String(text).replace(/\%VERSION\%/mg, version);
      }
   }])

   .filter('reverse', function() {
      return function(items) {
         return items.slice().reverse();
      };
   })

   .filter("keyboardShortcut", function(){
   return function(shortcut, $scope){
      if ($scope.editorPlatform == "mac"){
         return shortcut.replace(/Ctrl/g,'^').replace(/Command/g,'&#8984;').replace(/Shift/g,'&#8679;').replace(/Option/g,'&#8997;').replace(/Alt/g,'&#8997').replace(/Backspace/g,'&#9003;').replace(/Delete/g,'&#9003;').replace(/-/g,'<span class="space">&nbsp;</span>').replace(/\|/g,' | ');
      }else{
         return shortcut;
      }
   }
  })

   .filter('linkyimgy', [function() {
  var LINKY_URL_REGEXP =
        /((ftp|https?):\/\/|(mailto:)?[A-Za-z0-9._%+-]+@)\S*[^\s.;,(){}<>]/,
      MAILTO_REGEXP = /^mailto:/;

  return function(text, target) {
    if (!text) return text;
    var match;
    var raw = text;
    var html = [];
    var url;
    var i;
    while ((match = raw.match(LINKY_URL_REGEXP))) {
      // We can not end in these as they are sometimes found at the end of the sentence
      url = match[0];
      // if we did not match ftp/http/mailto then assume mailto
      if (match[2] == match[3]) url = 'mailto:' + url;
      i = match.index;
      console.log(url);
      addText(raw.substr(0, i));
      addLink(url, match[0].replace(MAILTO_REGEXP, ''));
      raw = raw.substring(i + match[0].length);
    }
    addText(raw);
    return html.join('');

    function addText(text) {
      if (!text) {
        return;
      }
      console.log(text);
      html.push(text);
    }

    function addLink(url, text) {
      html.push('<a ');
      if (angular.isDefined(target)) {
        html.push('target="');
        html.push(target);
        html.push('" ');
      }
      html.push('href="');
      html.push(url);
      html.push('">');
      console.log(url, /(jpeg|jpg|gif|png)$/.test(url));
      if (/(jpeg|jpg|gif|png)$/.test(url)){
        html.push('<img src="'+url+'" class="chat-image" />');
      }else{
        addText(text);
      }
      html.push('</a>');
    }
  };
}])

   ;
