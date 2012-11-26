'use strict';

/* Services */
  
angular.module('hakemusServices', ['ngResource']).
    factory('Hakemus', function($resource){
    var context = location.pathname.split('/')[1];
    context = context === 'virkailija_poc' ? '' : '/' + context;
  return $resource(context + '/hakemus/:oid', {oid:'@oid'},
   {'get':    {method:'GET'}});
});


