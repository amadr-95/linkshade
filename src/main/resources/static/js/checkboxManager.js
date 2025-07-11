document.addEventListener('DOMContentLoaded', function () {
    const selectAllCheckboxButton = document.getElementById('selectAll');
    const urlCheckboxes = document.querySelectorAll('.url-checkbox');
    const userCheckboxes = document.querySelectorAll('.user-checkbox');
    const presentCheckboxes = urlCheckboxes.length ? urlCheckboxes : userCheckboxes;
    const deleteButton = document.getElementById('deleteSelectedBtn');

    function updateDeleteButtonState() {
        const anyChecked = Array.from(presentCheckboxes).some(checkbox => checkbox.checked);
        deleteButton.disabled = !anyChecked;
    }

    selectAllCheckboxButton.addEventListener('change', (event) => {
        const isChecked = event.target.checked;
        presentCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });
        updateDeleteButtonState();
    })

    presentCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            updateDeleteButtonState();
            //if all are checked, also check the select all one
            selectAllCheckboxButton.checked = Array.from(presentCheckboxes).every(checkbox => checkbox.checked);
        })
    })
});
