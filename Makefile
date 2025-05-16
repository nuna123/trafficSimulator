SRC_DIR = srcs
OUT_DIR = out

COMPILER = javac

all: ${OUT_DIR}/Main.class

${OUT_DIR}/Main.class: ${SRC_DIR}/Main.java
	@mkdir -p ${OUT_DIR}
	${COMPILER} -d ${OUT_DIR} -cp ${SRC_DIR} ${SRC_DIR}/Main.java
	@echo "Created ${OUT_DIR}/Main.class"




run: ${NAME}
	@java -cp ${OUT_DIR} Main

clean:
	@rm -rf ${OUT_DIR}
	@echo "Cleaned up the output directory."

re: clean all
	@echo "Rebuilt project."

