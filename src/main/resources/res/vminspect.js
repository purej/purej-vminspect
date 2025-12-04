window.addEventListener('load', () => {
  registerShowHide();
  xsrfToForm();
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

function xsrfToForm() {
  const cookie = getCookie('XSRF-TOKEN');
  if (cookie) {
    for (const form of document.getElementsByTagName('form')) {
      if (form.method && form.method.toUpperCase() === 'POST') {
        const xsrfField = document.createElement('input');
        xsrfField.setAttribute('type', 'hidden');
        xsrfField.setAttribute('name', 'X-XSRF-TOKEN');
        xsrfField.setAttribute('value', cookie);
        form.appendChild(xsrfField);
      }
    }
  }
}

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    return parts.pop().split(';').shift();
  }
  return undefined;
}
