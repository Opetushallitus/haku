'use strict';

// Declare app level module which depends on filters, and services
angular.module('virkailija', ['virkailija.filters', 'virkailija.services', 'virkailija.directives']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/search', {templateUrl: 'partials/search.html', controller: SearchCtrl});
    $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: MyCtrl2});
	$routeProvider.when('/hakemus', {templateUrl: 'partials/hakemus.html', controller: HakemusCtrl});
    $routeProvider.otherwise({redirectTo: '/search'});
  }]).factory('Config', function(){
        var c = location.pathname.split('/')[1];
        c = c === 'virkailija' ? '' : '/' + c;

        return {
            context : c
        };
  });
