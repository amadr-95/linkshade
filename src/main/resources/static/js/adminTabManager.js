document.addEventListener('DOMContentLoaded', function() {
    const urlsTab = document.getElementById('urlsTab');
    const usersTab = document.getElementById('usersTab');
    const path = window.location.pathname;

    urlsTab.classList.toggle('active', path.endsWith('/urls'));
    usersTab.classList.toggle('active', path.endsWith('/users'));
});