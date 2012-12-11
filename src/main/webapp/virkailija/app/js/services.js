'use strict';

/* Services */


angular.module('virkailija.services', ['ngResource']).
    factory('Application', function($resource){
        return $resource('/haku/hakemukset', {}, {
            query: {method:'GET', isArray:true}
        });
    });
