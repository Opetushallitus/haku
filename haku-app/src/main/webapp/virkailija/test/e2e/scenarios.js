'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('virkailija', function() {

  beforeEach(function() {
    browser().navigateTo('../../app/index.html');
  });


  it('should automatically redirect to /search when location hash/fragment is empty', function() {
    expect(browser().location().url()).toBe("/search");
  });


  describe('search', function() {

    beforeEach(function() {
      browser().navigateTo('#/search');
    });


    it('should render search when user navigates to /search', function() {
      expect(element('#searchform').count()).toBe(1);
    });

  });


  describe('view2', function() {

    beforeEach(function() {
      browser().navigateTo('#/view2');
    });


    it('should render view2 when user navigates to /view2', function() {
      expect(element('[ng-view] p:first').text()).
        toMatch(/partial for view 2/);
    });

  });
});
