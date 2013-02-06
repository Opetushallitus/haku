'use strict';

/* Controllers */


function SearchCtrl($scope, Application, Config) {
    $scope.context = Config.context;

    $scope.search = function() {
        $scope.applications = Application.query({q: $scope.q, appState: $scope.applicationState,
            fetchPassive: $scope.fetchPassive, appPreference: $scope.applicationPreference});
    };

    $scope.reset = function() {
        $scope.applications = [];
        $scope.q = '';
        $scope.applicationState = '';
        $scope.fetchPassive = false;
        $scope.applicationPreference = '';
    };

    $scope.reset();
}


function MyCtrl2() {
}
MyCtrl2.$inject = [];
