'use strict';

/* Services */


angular.module('virkailija.services', ['ngResource']).
    factory('Application', function($resource){
        return $resource('hakemukset/hakemukset.json', {}, {
            query: {method:'GET', params:{}, isArray:true}
        });
    });
