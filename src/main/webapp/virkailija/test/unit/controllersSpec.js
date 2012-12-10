'use strict';

/* jasmine specs for controllers go here */

describe('Controllers', function(){

    beforeEach(function(){
        this.addMatchers({
            toEqualData: function(expected) {
                return angular.equals(this.actual, expected);
            }
        });
    });

    beforeEach(module('virkailija.services'));

    describe('SearchCtrl', function(){
        var scope, ctrl, $httpBackend;

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
            $httpBackend = _$httpBackend_;
            scope = $rootScope.$new();
            ctrl = $controller(SearchCtrl, {$scope: scope});
        }));


        it('should search "applications" model with 2 application fetched', function() {
            expect(scope.applications).toEqual([]);
            scope.search();
            expect(scope.applications.length).toBe(2);
            expect(scope.applications).toEqualData([{vastaukset:{henkilotiedot:{Sukunimi: "Testaaja", Etunimet: "Teppo Topias", Henkilotunnus: "120187-1234"}}, oid : "1.2.3.4.5.1", state: "Voimassa"},
                {vastaukset:{henkilotiedot:{Sukunimi: "Virtanen", Etunimet: "Vesa Matti", Henkilotunnus: "130382-1112"}}, oid : "1.2.3.4.5.2", state: "Voimassa"}]);
        });


        it('should clear all the fetched applications when reset called', function() {
            scope.search();
            scope.reset();
            expect(scope.applications).toEqual([]);
        });
    });
});


describe('MyCtrl2', function(){
  var myCtrl2;


  beforeEach(function(){
    //myCtrl2 = new MyCtrl2();
  });


  it('should ....', function() {
    //spec body
  });
});
