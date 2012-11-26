'use strict';

/* Controllers */

function HakemusCtrl($scope, $http, Hakemus) {
    $scope.search = function() {
        /* $http.jsonp('http://localhost:8080/haku/hakemus/1.2.3.4.5.1?callback=JSON_CALLBACK').success(function(data) {
  console.log(data.found);        
});*/
        $scope.hakemus = Hakemus.get({oid: $scope.oid});
    };
};
