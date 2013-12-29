// Simple function to show or hide the element with the given id
function showHide(id) {
  if (document.getElementById(id).style.display=='none') {
    if (document.getElementById(id + 'Img') != null) {
      document.getElementById(id + 'Img').src='?resource=bullets/minus.png';
    }
    document.getElementById(id).style.display='inline';
  } else {
    if (document.getElementById(id + 'Img') != null) {
      document.getElementById(id + 'Img').src='?resource=bullets/plus.png';
    }
    document.getElementById(id).style.display='none';
  }
}
