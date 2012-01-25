// # Testing
//
// To test this project ensure that [phantomjs] is installed, then compile the
// clojurescript to a single file `out/all.js`, then run the tests with
// `phantomjs test.js`.
//
// [phantomjs]: http://phantomjs.org

phantom.injectJs('jasmine/jasmine.js');
phantom.injectJs('jasmine/ConsoleReporter.js');
phantom.injectJs('quickcheck/qc.js');
phantom.injectJs('out/all.js');

var fs = require('fs');

maybeTestQc();

jasmine.getEnv().addReporter(
    new jasmine.ConsoleReporter(
      function(msg) { fs.write('/dev/stdout', msg, 'w'); },
      function(runner) {
        try {
          if (runner.results().passed()) phantom.exit(0);
          else phantom.exit(runner.results().failedCount);
        } catch (e) {
          phantom.exit(1);
        }
      },
      true));
try {
  jasmine.getEnv().execute();
} catch (e) {
  phantom.exit(1);
}

function maybeTestQc() {
  if (typeof qc !== 'undefined' &&
      typeof qc.allProps !== 'undefined') {
    var conf = new qc.Config({});
    describe('quickcheck tests', function() {
      beforeEach(function() {
        this.addMatchers({
          toPass: function() {
            var result = this.actual;
            this.message = function() {
              return result.name+' failed on '+result.failedCase+
                (result.shrinkedArgs ? ' mincase '+result.shrinkedArgs : '');
            };
            return result.status !== 'fail';
          }
        });
      });
      it('should pass', function() {
        for (var i = 0; i < qc.allProps.length; ++i) {
          var result = qc.allProps[i].run(conf);
          expect(result).toPass();
        }
      });
    });
  }
}
