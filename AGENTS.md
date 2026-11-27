# TalentLink contributor instructions

## Scope and workflow

- Resolve the repository root with `git rev-parse --show-toplevel` and verify that it contains `pom.xml`. Read applicable instructions and run `git status --short --branch` before changing anything. Inspect staged changes and preserve unrelated work.
- Extract the task ID, deliverables, acceptance criteria, dependencies, and stop conditions. Change only what the task needs. Follow explicit limits such as "only these files," "stop before commit," or a required review gate. Do not invent gates or treat silence as approval.
- Fetch `origin/main` before choosing a base. Use an existing task branch if appropriate, or create `feature/<lowercase-task-id>-<short-name>`. Use current `origin/main` for independent work. If a required change is still on an open PR, use its branch as the base when that keeps the diff focused. Document any additional dependency included in the branch. Never merge a remote PR to unblock yourself.
- By default, carry the task through implementation, testing, fixes, commit, push, and PR creation without routine confirmation. Stop for a genuine blocker, missing authorization, destructive action, or explicit user stop condition.
- Before committing, review the worktree and staged diff, run whitespace checks, and stage only intended paths. Include the task ID in the commit and PR title, using `feat: <TASK-ID> ...`, `fix: <TASK-ID> ...`, or `docs: <TASK-ID> ...`. Push the task branch, check for an existing PR for that head, and reuse it instead of opening a duplicate. Verify the PR URL, base, head SHA, and scope on GitHub. Never auto-merge.
- End with the verified PR URL, full commit SHA, and actual test results. If publication or testing was blocked or explicitly deferred, report what remains and why. Never claim an unverified URL, commit, or passing check.

## Implementation

- Use JDK 17 and the checked-in Maven Wrapper. Confirm the runtime with `./mvnw --version`. Keep Spring dependency versions managed by the Boot parent unless a dependency requires its own version.
- Match existing Spring MVC, Thymeleaf fragments, Bootstrap WebJars, Bean Validation, and JdbcClient patterns. Reuse the layout and flash messages. Attach validation feedback to each field and keep safe input on error. Never echo passwords, OTPs, or stack traces.
- Keep CSRF on state-changing forms. Derive identity and record ownership from the authenticated principal. Enforce roles and ownership on the server, including the write query where applicable. Use existing route and role mappings. Keep related writes atomic and rely on database constraints for concurrent duplicates.
- Put schema migrations in `src/main/resources/db/migration`. Add the next available Flyway version and preserve applied migrations. Keep synthetic seed data in `src/main/resources/db/dev` and verify the dev-only configuration. Keep `ddl-auto=validate`. Use `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` for local configuration. Keep real credentials, temporary test credentials, OTP logs, and disposable database files out of commits.

## Verification and recovery

- Build with `./mvnw clean package`. For runtime changes, start the current build on localhost with profile `dev` as appropriate. Use a disposable MySQL database for migration and write tests. Check startup logs and HTTP responses. When possible, test both an empty schema and an upgrade from the previous version.
- Exercise the real paths relevant to the task, including valid and invalid input, role and ownership boundaries, CSRF, and rollback or concurrency when affected. Verify desktop and mobile rendering for UI changes. Render documentation diagrams when tooling is available and compare them with the documented schema.
- When a check fails, inspect the root cause, correct the smallest relevant issue, and rerun the failed check and affected regressions. If an offline build lacks artifacts, allow Maven to resolve them through the authorized network path. If a port serves a stale process, verify the running build or use another localhost port. If MySQL initialization fails, inspect its logs and retry in a fresh disposable directory with a working local version.
- If Git cannot write metadata or the machine blocks local sockets, use the approved execution path. If terminal Git lacks credentials, use the authenticated IntelliJ Git and PR integration when available. Recheck remote state after a timeout before retrying a push or creating a PR. Never extract or print credentials.

## Kiến trúc và phối hợp Worker

- Trước khi viết code, đọc [kiến trúc](docs/architecture/ARCHITECTURE.md) và tiêu chuẩn liên quan. Tìm chức năng hiện có, tái sử dụng thành phần phù hợp. Không tự ý đổi kiến trúc, thêm framework hoặc dependency, tạo abstraction thiếu nhu cầu, sao chép business logic hoặc sửa ngoài phạm vi.
- Áp dụng [tiêu chuẩn mã và UI](docs/architecture/CODING_STANDARDS.md), [bảo mật](docs/architecture/SECURITY_STANDARDS.md) và [MySQL/Flyway](docs/architecture/DATABASE_STANDARDS.md). Kiểm tra phân quyền backend, không hardcode bí mật, không sửa migration đã áp dụng và điều phối phiên bản trước khi viết migration.
- Một task, một Chat Codex, một branch và một worktree. Tách DB, port và kho tệp. Chốt quyền sửa vùng dùng chung, migration và base PR theo [quy trình song song](docs/architecture/PARALLEL_DEVELOPMENT.md).
- Viết test cho chức năng mới và lỗi đã sửa. Chạy regression trước commit, tự review git diff trước push và đối chiếu [Quality Gate](docs/architecture/TESTING_STANDARDS.md). Không tự động merge hoặc chỉnh sửa PR minh chứng cũ. Báo cáo kết quả và giới hạn bằng tiếng Việt.
