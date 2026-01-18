/**
 * @type {import('semantic-release').GlobalConfig}
 */
module.exports = {
  branches: ['1.21.1/main'],
  tagFormat: "1.21.1-v${version}",
  plugins: [
    [
      '@semantic-release/commit-analyzer',
      {
        preset: 'angular',
        releaseRules: [
          { type: 'tweak', release: 'patch' }
        ]
      }
    ],
    // --------------------
    [
      '@semantic-release/release-notes-generator',
      {
        preset: 'conventionalcommits',
        presetConfig: {
          types: [
            { type: 'feat', section: '✨ Features' },
            { type: 'fix', section: '🐛 Bug Fixes' },
            { type: 'perf', section: '⚡ Performance Improvements' },
            { type: 'revert', section: '↩️ Reverts' },
            { type: 'tweak', section: '⚙️ Tweaks', hidden: false },
            { type: 'docs', section: '📝 Documentation', hidden: true },
            { type: 'style', section: '💈 Styles', hidden: true },
            { type: 'chore', section: '🧹 Miscellaneous Chores', hidden: true },
            { type: 'refactor', section: '🪄 Code Refactoring', hidden: true },
            { type: 'test', section: '✅ Tests', hidden: true },
            { type: 'ci', section: '🔁 Continuous Integration', hidden: true },
          ],
        },
      },
    ],
    // --------------------
    [
      '@semantic-release/changelog',
      {
        changelogFile: 'CHANGELOG.md',
      },
    ],
    // --------------------
    './update-version.js',
    // --------------------
    [
      '@semantic-release/exec',
      {
        prepareCmd: './gradlew build --build-cache',
      },
    ],
    // --------------------
    [
      '@semantic-release/github',
      {
        assets: [
          'fabric/build/libs/!(*-@(dev-shadow|sources)).jar',
          'neoforge/build/libs/!(*-@(dev-shadow|sources)).jar',
        ],
      },
    ],
    // --------------------
    [
      '@semantic-release/git',
      {
        assets: [
          'gradle.properties',
        ],
        message: 'chore(release): ${nextRelease.version} [skip ci]',
      },
    ],
    // --------------------
    'semantic-release-export-data',
  ],
};
