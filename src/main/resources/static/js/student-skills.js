const search = document.querySelector('#skill-search');
const select = document.querySelector('#skillId');
const options = Array.from(select.options).slice(1);
const status = document.querySelector('#skill-search-status');

search.hidden = false;
search.labels[0].hidden = false;
search.addEventListener('input', () => {
    const query = search.value.trim().toLocaleLowerCase('vi');
    const selected = select.value;
    const matches = options.filter(option => option.text.toLocaleLowerCase('vi').includes(query));
    select.replaceChildren(select.options[0], ...matches);
    select.value = matches.some(option => option.value === selected) ? selected : '';
    status.textContent = matches.length ? `${matches.length} kỹ năng phù hợp.` : 'Không tìm thấy kỹ năng.';
});
