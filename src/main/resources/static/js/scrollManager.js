// Save scroll position only if needed
window.addEventListener('beforeunload', () => {
    if (sessionStorage.getItem('shouldRestoreScroll') === 'true') {
        sessionStorage.setItem('scrollToTable', 'true');
    }
});

// Go to the table position when reloading the page
window.addEventListener('load', () => {
    if (sessionStorage.getItem('scrollToTable') === 'true') {
        const paginationNavbar = document.getElementById('pagination-navbar');
        if (paginationNavbar) {
            setTimeout(() => {
                paginationNavbar.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 100);
        }
        sessionStorage.removeItem('scrollToTable');
    }
    sessionStorage.removeItem('shouldRestoreScroll');
});