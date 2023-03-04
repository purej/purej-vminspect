window.addEventListener('load', () => {
  registerShowHide();
});

// Register the show/hide links
function registerShowHide() {
  let links = document.querySelectorAll('[showHide]');
  for (const link of links) {
    const id = link.getAttribute('showHide');
    link.addEventListener('click', function(event) {
      showHide(id);
    });
  }
}

function showHide(id) {
  const el = document.getElementById(id);
  if (el) {
    const imgEl = document.getElementById(id + 'Img');
    if (el.classList.contains('hidden')) {
      el.classList.remove('hidden');
      if (imgEl) {
        imgEl.src='?resource=bullets/minus.png';
      }
    } else {
      el.classList.add('hidden');
      if (imgEl) {
        imgEl.src='?resource=bullets/plus.png';
      }
    }
  }
}
