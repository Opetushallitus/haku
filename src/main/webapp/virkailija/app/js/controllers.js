'use strict';

/* Controllers */


function SearchCtrl($scope, Application) {
    $scope.applications = [];

    $scope.search = function() {
        $scope.applications = [{vastaukset:{henkilotiedot:{Sukunimi: "Testaaja", Etunimet: "Teppo Topias", Henkilotunnus: "120187-1234"}}, oid : "1.2.3.4.5.1", state: "Voimassa"},
            {vastaukset:{henkilotiedot:{Sukunimi: "Virtanen", Etunimet: "Vesa Matti", Henkilotunnus: "130382-1112"}}, oid : "1.2.3.4.5.2", state: "Voimassa"}];
        //$scope.applications = Application.query();
    };

    $scope.reset = function() {
        $scope.applications = [];
    };
};


function MyCtrl2() {
}
MyCtrl2.$inject = [];
