document.addEventListener('DOMContentLoaded', function () {
    // for editing URLs in my-url and admin-dashboard
    const datePickerButtons = document.querySelectorAll('[id^="openExpirationDatePicker-"]');
    // for creating the URL in index when the user is authenticated
    const input = document.getElementById('expirationDate');
    const btn = document.getElementById('openExpirationDatePicker');

    function showDatePicker(input, e) {
        e.preventDefault();
        if (typeof input.showPicker === 'function') {
            input.showPicker();
        } else {
            input.focus();
            input.click();
        }
    }

    if (btn) {
        btn.addEventListener('click', (e) => showDatePicker(input, e));
    }

    if (datePickerButtons) {
        datePickerButtons.forEach(btn => {
            const id = btn.id.replace('openExpirationDatePicker-', '');
            const input = document.getElementById(`expirationDate-${id}`);
            btn.addEventListener('click', (e) => showDatePicker(input, e));
        })
    }
})
