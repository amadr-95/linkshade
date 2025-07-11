document.addEventListener('DOMContentLoaded', function () {
    const lengthRange = document.getElementById('urlLength');
    const lengthValue = document.getElementById('urlLengthValue');
    const modeSwitch = document.getElementById('customModeSwitch');
    const rangeContainer = document.querySelector('.range-container');
    const customContainer = document.querySelector('.input-container');

    function updateRangeDisplay() {
        lengthValue.innerText = lengthRange.value;
    }

    // change between input fields
    function toggleInputMode(animate = false) {
        const showElement = modeSwitch.checked ? customContainer : rangeContainer;
        const hideElement = modeSwitch.checked ? rangeContainer : customContainer;

        if (!animate) {
            // init
            hideElement.classList.add('d-none');
            showElement.classList.remove('d-none');
            showElement.style.opacity = '1';
            return;
        }

        hideElement.style.opacity = '0';
        hideElement.style.transition = 'opacity 0.2s';

        setTimeout(() => {
            hideElement.classList.add('d-none');
            showElement.classList.remove('d-none');
            showElement.style.opacity = '0';

            requestAnimationFrame(() => {
                showElement.style.opacity = '1';
                showElement.style.transition = 'opacity 0.2s';
            });
        }, 200);
    }

    lengthRange.addEventListener("input", updateRangeDisplay);
    modeSwitch.addEventListener('change', () => toggleInputMode(true));

    updateRangeDisplay();
    toggleInputMode();
});