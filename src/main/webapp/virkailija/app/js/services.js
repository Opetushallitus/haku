'use strict';

/* Services */


angular.module('virkailija.services', ['ngResource']).
    factory('Application', function($resource){
        var context = location.pathname.split('/')[1];
        context = context === 'virkailija' ? '' : '/' + context;
        return $resource(context + '/hakemukset', {}, {
            query: {method:'GET', isArray:true}
        });
    });
