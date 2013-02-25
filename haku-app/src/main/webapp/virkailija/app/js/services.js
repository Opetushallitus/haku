'use strict';

/* Services */


angular.module('virkailija.services', ['ngResource']).
    factory('Application', function($resource, Config){
        return $resource(Config.context + '/applications', {}, {
            query: {method:'GET', isArray:true}
        });
    });

