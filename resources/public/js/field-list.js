function addInput(elementId, namePrefix) {
  // TODO: this is pretty naive and obviously a tad silly.
  //       let's find an off-the-shelf way to do this? -sd

  var toNum = function(s, prefix) {
    return parseInt(s.replace(new RegExp(prefix), ''));
  };

  var nextName = function(prefix) {
    var nodes = document.querySelectorAll(`[name^="${prefix}"]`);
    var translations = Array.prototype.slice.call(nodes);
    var fieldCounts = translations.map(e => toNum(e.name, prefix));
    var biggest = fieldCounts.sort().reverse()[0] || 0;
    var next = biggest + 1;
    return prefix + next;
  };

  var container = document.getElementById(elementId);
  var input = document.createElement("input");
  input.type = "text";
  input.name = nextName(namePrefix);
  container.appendChild(input);
}
