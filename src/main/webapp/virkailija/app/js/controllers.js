'use strict';

/* Controllers */


function SearchCtrl($scope, Application, Config) {
    $scope.applications = [];
    $scope.term = '';
    $scope.context = Config.context;

    $scope.search = function() {
        $scope.applications = Application.query({term: $scope.term});
    };

    $scope.reset = function() {
        $scope.applications = [];
    };
}


function MyCtrl2() {
}
MyCtrl2.$inject = [];
