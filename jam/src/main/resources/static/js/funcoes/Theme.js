function setThemeIcon(isDark) {
    const icon = document.getElementById('theme-icon');
    icon.textContent = isDark ? 'dark_mode' : 'light_mode';
}

(function() {
    const theme = localStorage.getItem('theme');
    const useDark = theme === 'dark'
        || (!theme && window.matchMedia('(prefers-color-scheme: dark)').matches);
    if (useDark) {
        document.documentElement.classList.add('dark');
    }
    setThemeIcon(useDark);
})();

function toggleTheme() {
    const html = document.documentElement;
    const isDarkNow = html.classList.contains('dark');

    if (isDarkNow) {
        html.classList.remove('dark');
        localStorage.setItem('theme', 'light');
    } else {
        html.classList.add('dark');
        localStorage.setItem('theme', 'dark');
    }

    setThemeIcon(!isDarkNow);
}