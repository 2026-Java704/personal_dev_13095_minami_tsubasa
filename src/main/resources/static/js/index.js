/**
 * タブによる切り替え実装
 */
function switchTab(tabId) {
  // すべてのボタンとコンテンツから active クラスを削除
  document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
  document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));

  // クリックされたボタンとコンテンツに active クラスを追加
  event.currentTarget.classList.add('active');
  document.getElementById(tabId).classList.add('active');
}